package com.github.tscz.spring.platform;

import static com.github.tscz.spring.platform.BDDJsonAssertions.and;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.tscz.spring.platform.auth.Authorities;
import com.github.tscz.spring.platform.auth.User;
import com.github.tscz.spring.platform.jwt.JwtRequest;
import com.github.tscz.spring.platform.jwt.JwtResponse;
import com.github.tscz.spring.platform.testentity.AuthoritiesRepository;
import com.github.tscz.spring.platform.testentity.TestEntity;
import com.github.tscz.spring.platform.testentity.TestEntityRepository;
import com.github.tscz.spring.platform.testentity.UserRepository;

import net.javacrumbs.jsonunit.core.Option;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestApplication.class)
@ContextConfiguration(initializers = {ApplicationTests.Initializer.class})
public class ApplicationTests {

	@Container
	public static PostgreSQLContainer<?> postgresContainer = //
			new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))//
					.withDatabaseName("foo")//
					.withUsername("foo")//
					.withPassword("secret");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(//
					"spring.datasource.url=" + postgresContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgresContainer.getUsername(),
					"spring.datasource.password=" + postgresContainer.getPassword(),
					"spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false"
			//
			).applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TestEntityRepository testEntityRepository;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AuthoritiesRepository authoritiesRepo;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {

		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);
		beans.forEach((name, bean) -> System.out.println(name));
		then(postgresContainer.isRunning()).isTrue();
	}

	@Test
	@SuppressWarnings("static-access")
	public void accessGrantedWithValidToken() {
		var testEntity = new TestEntity();
		testEntity.setTitle("Title");
		testEntity.setDescription("Description");

		testEntityRepository.save(testEntity);

		userRepo.save(new User("admin", "{bcrypt}$2a$10$MfijVmidq73sKdVNthcchObA2WUg5tlkralABIqoLVx359AOcqda2", true));
		authoritiesRepo.save(new Authorities("admin", "ROLE_ADMIN"));
		authoritiesRepo.save(new Authorities("admin", "ROLE_USER"));

		// Retrieve bearer token
		var jwtRequest = new JwtRequest("admin", "password");
		var jwtResponse = restTemplate.postForEntity("/token", jwtRequest, JwtResponse.class);
		var token = jwtResponse.getBody().token();
		then(jwtResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(token).isNotEmpty();

		// Request without bearer token
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		var response = restTemplate.exchange(//
				"/testEntities", //
				HttpMethod.GET, //
				new HttpEntity<>(headers), //
				String.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		and.then(response.getBody())//
				.isObject()//
				.containsEntry("type", "about:blank")//
				.containsEntry("status", BigDecimal.valueOf(HttpStatus.BAD_REQUEST.value()))//
				.containsEntry("title", "Constraint Violation");

		and.then(response.getBody())//
				.when(Option.IGNORING_ARRAY_ORDER)//
				.node("violations")//
				.isArray()//
				.isEqualTo(json("[{\"field\": \"Request Header\",\"message\": \"Authorization header missing\"}]"));

		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		var response2 = restTemplate.exchange(//
				"/testEntities", //
				HttpMethod.GET, //
				new HttpEntity<>(headers), //
				TestEntity[].class);

		System.out.println(response2);

		then(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(response2.getBody()).containsExactly(testEntity);
	}

}
