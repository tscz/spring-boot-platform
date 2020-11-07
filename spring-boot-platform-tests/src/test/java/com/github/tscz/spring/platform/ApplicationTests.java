package com.github.tscz.spring.platform;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.zalando.problem.violations.ConstraintViolationProblem;

import com.github.tscz.spring.platform.jwt.JwtRequest;
import com.github.tscz.spring.platform.jwt.JwtResponse;
import com.github.tscz.spring.platform.testentity.TestEntity;
import com.github.tscz.spring.platform.testentity.TestEntityRepository;

import net.javacrumbs.jsonunit.core.Option;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestApplication.class)
public class ApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private TestEntityRepository testEntityRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {

		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);
		beans.forEach((name, bean) -> System.out.println(name));
	}

	// @Test
	public void accessGrantedWithValidToken() {
		var testEntity = new TestEntity();
		testEntity.setTitle("Title");
		testEntity.setDescription("Description");
		var expectedBody = Arrays.asList(testEntity);

		given(testEntityRepository.findAll()).willReturn(expectedBody);

		// Retrieve bearer token
		var jwtRequest = new JwtRequest("admin", "password");
		var jwtResponse = restTemplate.postForEntity("/token", jwtRequest, JwtResponse.class);
		var token = jwtResponse.getBody().token();
		assertThat(jwtResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(token).isNotEmpty();

		// Request without bearer token
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		var response = restTemplate.exchange(//
				"/testEntity", //
				HttpMethod.GET, //
				new HttpEntity<>(headers), //
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThatJson(response.getBody())//
				.isObject()//
				.containsEntry("type", ConstraintViolationProblem.TYPE_VALUE)//
				.containsEntry("status", BigDecimal.valueOf(HttpStatus.BAD_REQUEST.value()))//
				.containsEntry("title", "Constraint Violation");

		assertThatJson(response.getBody())//
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

		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response2.getBody()).isEqualTo(expectedBody.toArray());
	}

}
