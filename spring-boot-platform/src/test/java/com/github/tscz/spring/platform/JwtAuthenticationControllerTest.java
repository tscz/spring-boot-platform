package com.github.tscz.spring.platform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.zalando.problem.Problem;

import com.github.tscz.spring.platform.jwt.JwtRequest;
import com.github.tscz.spring.platform.jwt.JwtResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class JwtAuthenticationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void noTokenForUnknownUser() {
		var request = new JwtRequest("unknown", "unknown");

		var response = restTemplate.postForEntity("/token", request, Problem.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		System.out.println(response.getBody());
	}

	@Test
	public void noTokenForKnownUserWithWrongPassword() {
		var request = new JwtRequest("admin", "unknown");

		var response = restTemplate.postForEntity("/token", request, Problem.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		System.out.println(response.getBody());
	}

	@Test
	public void tokenReturnedForKnownUser() {
		var jwtRequest = new JwtRequest("admin", "password");

		var response = restTemplate.postForEntity("/token", jwtRequest, JwtResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().token()).isNotEmpty();
	}

}
