package com.github.tscz.spring.platform.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.tscz.spring.platform.exception.Exceptions;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsManager userDetailsManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		if (requestTokenHeader == null) {
			SecurityContextHolder.clearContext();
			throw Exceptions.constraintViolation("Request Header", "Authorization header missing");
		}

		if (!requestTokenHeader.startsWith("Bearer ")) {
			SecurityContextHolder.clearContext();
			throw Exceptions.constraintViolation("Request Header", "JWT Token does not begin with Bearer String");
		}

		var jwtToken = requestTokenHeader.substring(7);

		var claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);

		var username = claims.getSubject();

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsManager.loadUserByUsername(username);

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