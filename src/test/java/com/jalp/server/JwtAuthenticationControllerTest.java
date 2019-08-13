package com.jalp.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.jalp.server.model.JwtRequest;
import com.jalp.server.model.JwtResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JwtAuthenticationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void exampleTest() {

		// see https://github.com/spring-projects/spring-framework/issues/14004
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
			public boolean hasError(ClientHttpResponse response) throws IOException {
				HttpStatus statusCode = response.getStatusCode();
				return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
			}
		});

		JwtRequest jwtRequest = new JwtRequest();
		jwtRequest.setPassword("password");
		jwtRequest.setUsername("admin");

		ResponseEntity<JwtResponse> response2 = this.restTemplate.postForEntity("/token", jwtRequest,
				JwtResponse.class);

		assertThat(response2.getBody()).isEqualTo("Hello World");
	}

}
