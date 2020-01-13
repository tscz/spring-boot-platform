package com.github.tscz.spring.platform.config;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

@ConfigurationProperties(prefix = "com.github.tscz.spring.platform")
@Validated
public class ApplicationConfig {

	@NestedConfigurationProperty
	private JwtConfig jwt = new JwtConfig();

	public static class JwtConfig {

		@NotNull
		private String secret;

		@NotNull
		private int expiration;

		public Key getSecret() {
			Preconditions.checkNotNull(secret, "JWT Secret must be set for Authorization.");

			try {
				return Keys.hmacShaKeyFor(secret.getBytes("UTF-8"));
			} catch (WeakKeyException | UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		public int getExpiration() {
			return expiration;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public void setExpiration(int expiration) {
			this.expiration = expiration;
		}

	}

	public Key getJwtSecret() {
		return jwt.getSecret();
	}

	/**
	 * @return JWT expiration time in milliseconds
	 */
	public int getJwtExpiration() {
		return jwt.getExpiration() * 1000;
	}

	public JwtConfig getJwt() {
		return jwt;
	}

	public void setJwt(JwtConfig jwt) {
		this.jwt = jwt;
	}

}
