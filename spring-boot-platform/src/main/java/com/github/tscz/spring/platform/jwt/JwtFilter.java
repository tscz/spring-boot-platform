package com.github.tscz.spring.platform.jwt;

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
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JdbcUserDetailsManager jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

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

		var jwtToken = requestTokenHeader.substring(7);

		var claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);

		var username = claims.getSubject();

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);

		}
		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.startsWith("/token");
	}

}