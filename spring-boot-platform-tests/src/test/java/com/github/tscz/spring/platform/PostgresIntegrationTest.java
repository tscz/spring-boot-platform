package com.github.tscz.spring.platform;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.tscz.spring.platform.testentity.TestEntity;
import com.github.tscz.spring.platform.testentity.TestEntityRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestApplication.class)
@ContextConfiguration(initializers = {PostgresIntegrationTest.Initializer.class})
public class PostgresIntegrationTest {

	@Autowired
	TestEntityRepository repo;

	@Container
	public static PostgreSQLContainer<?> postgresContainer = //
			new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))//
					.withDatabaseName("foo")//
					.withUsername("foo")//
					.withPassword("secret");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + postgresContainer.getJdbcUrl(),
							"spring.datasource.username=" + postgresContainer.getUsername(),
							"spring.datasource.password=" + postgresContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@Test
	void test() {
		then(postgresContainer.isRunning()).isTrue();

		var entity = new TestEntity();
		entity.setTitle("This is a title");
		entity.setDescription("This is a description");

		repo.save(entity);

		then(repo.count()).isGreaterThan(0);
	}

}