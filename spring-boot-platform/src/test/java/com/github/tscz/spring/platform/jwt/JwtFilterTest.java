package com.github.tscz.spring.platform.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Collections;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.SignatureException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.problem.violations.ConstraintViolationProblem;

@ExtendWith(SpringExtension.class)
public class JwtFilterTest {
	private static final String token = "260bce87-6be9-4897-add7-b3b675952538";

	@Mock
	private JdbcUserDetailsManager userManager;

	@Mock
	private JwtTokenUtil jwtTokenUtil;

	@InjectMocks
	private JwtFilter tokenAuthenticationFilter;

	@Controller
	private static class DummyController {

		@GetMapping("/dummy")
		@ResponseBody
		public String dummy() {
			return "dummy";
		}

	}

	@Test
	public void returnForValidUser() throws Exception {

		when(jwtTokenUtil.getAllClaimsFromToken(token)).thenReturn(new DefaultClaims().setSubject("username"));
		when(userManager.loadUserByUsername("username"))
				.thenReturn(new User("username", "password", Collections.emptyList()));

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))//
				.andExpect(status().isOk());

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
	}

	@Test
	public void missingUser() throws Exception {

		when(jwtTokenUtil.getAllClaimsFromToken(token)).thenReturn(new DefaultClaims());

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))//
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void securityContextAlreadySet() throws Exception {

		when(jwtTokenUtil.getAllClaimsFromToken(token)).thenReturn(new DefaultClaims().setSubject("username"));

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))//
				.andExpect(status().isOk());
	}

	@Test
	public void missingAuthorizationHeader() throws Exception {

		assertThatThrownBy(() ->

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy")))//

						.isExactlyInstanceOf(ConstraintViolationProblem.class);
	}

	@Test
	public void invalidAuthorizationHeaderWithBearerMissing() throws Exception {

		assertThatThrownBy(() ->

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy")//
						.header(HttpHeaders.AUTHORIZATION,
								"This should start with 'Bearer ' followed by a valid token")))//

										.isExactlyInstanceOf(ConstraintViolationProblem.class);
	}

	@ParameterizedTest
	@ValueSource(classes = { //
			UnsupportedJwtException.class, //
			MalformedJwtException.class, //
			SignatureException.class, //
			ExpiredJwtException.class, //
			IllegalArgumentException.class})
	public void invalidTokenValue(Class<Exception> exception) throws Exception {

		when(jwtTokenUtil.getAllClaimsFromToken("InvalidToken")).thenThrow(exception);

		assertThatThrownBy(() ->

		standaloneSetup(new DummyController())//
				.addFilters(tokenAuthenticationFilter).build()//
				.perform(get("/dummy").header(HttpHeaders.AUTHORIZATION, "Bearer InvalidToken")))//

						.isExactlyInstanceOf(exception);
	}

}
