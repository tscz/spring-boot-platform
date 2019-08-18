package com.jalp.server;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import io.jsonwebtoken.ExpiredJwtException;

public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JdbcUserDetailsManager jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		if (requestTokenHeader == null) {
			SecurityContextHolder.clearContext();
			throw new ConstraintViolationProblem(Status.BAD_REQUEST,
					Arrays.asList(new Violation("Request Header", "Authorization header missing")));
		}

		if (!requestTokenHeader.startsWith("Bearer ")) {
			SecurityContextHolder.clearContext();
			throw new ConstraintViolationProblem(Status.BAD_REQUEST,
					Arrays.asList(new Violation("Request Header", "JWT Token does not begin with Bearer String")));
		}

		jwtToken = requestTokenHeader.substring(7);

		try {
			username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		} catch (IllegalArgumentException e) {
			SecurityContextHolder.clearContext();
			throw new ConstraintViolationProblem(Status.BAD_REQUEST,
					Arrays.asList(new Violation("Request Header", "Unable to get JWT Token")));
		} catch (ExpiredJwtException e) {
			SecurityContextHolder.clearContext();
			throw new ConstraintViolationProblem(Status.BAD_REQUEST,
					Arrays.asList(new Violation("Request Header", "JWT Token has expired")));
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.startsWith("/token");
	}

}