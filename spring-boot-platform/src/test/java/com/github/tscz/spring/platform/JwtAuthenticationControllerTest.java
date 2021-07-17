package com.github.tscz.spring.platform;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.zalando.problem.Problem;

import com.github.tscz.spring.platform.jwt.JwtRequest;
import com.github.tscz.spring.platform.jwt.JwtResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class JwtAuthenticationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private AuthenticationManager authenticationManager;

	@Test
	public void noTokenForUnknownUser() {
		var request = new JwtRequest("unknown", "unknown");
		given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("unknown", "unknown")))
				.willThrow(BadCredentialsException.class);

		var response = restTemplate.postForEntity("/token", request, Problem.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		System.out.println(response.getBody());
	}

	@Test
	public void tokenReturnedForKnownUser() {
		var jwtRequest = new JwtRequest("admin", "password");
		given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "password")))
				.willReturn(null);

		var response = restTemplate.postForEntity("/token", jwtRequest, JwtResponse.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(response.getBody().token()).isNotEmpty();
	}

}
