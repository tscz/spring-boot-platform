package com.jalp.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.jalp.server.model.JwtRequest;
import com.jalp.server.model.JwtResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JwtAuthenticationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setup() {
		// see https://github.com/spring-projects/spring-framework/issues/14004
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
			public boolean hasError(ClientHttpResponse response) throws IOException {
				HttpStatus statusCode = response.getStatusCode();
				return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
			}
		});
	}

	@Test
	public void noTokenForUnknownUser() {
		var request = new JwtRequest("unknown", "unknown");

		var response = this.restTemplate.postForEntity("/token", request, JwtResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody().getToken()).isNull();

	}

	@Test
	public void tokenReturnedForKnownUser() {
		var jwtRequest = new JwtRequest("admin", "password");

		var response = this.restTemplate.postForEntity("/token", jwtRequest, JwtResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getToken()).isNotEmpty();
	}

}
