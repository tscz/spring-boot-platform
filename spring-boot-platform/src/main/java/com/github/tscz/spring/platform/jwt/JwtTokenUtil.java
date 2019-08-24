package com.github.tscz.spring.platform.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tscz.spring.platform.config.ApplicationConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenUtil {

	@Autowired
	public ApplicationConfig config;

	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(config.getJwtSecret()).parseClaimsJws(token).getBody();
	}

	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return generateToken(claims, username);
	}

	private String generateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()//
				.setClaims(claims)//
				.setSubject(subject)//
				.setIssuedAt(new Date(System.currentTimeMillis()))//
				.setExpiration(new Date(System.currentTimeMillis() + config.getJwtExpiration()))//
				.signWith(config.getJwtSecret())//
				.compact();
	}
}