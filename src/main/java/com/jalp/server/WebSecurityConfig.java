package com.jalp.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requiresChannel().requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null).requiresSecure();

		http.authorizeRequests().anyRequest().authenticated().and().httpBasic();

		http.csrf().disable();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserDetails user = User.withUsername(System.getenv("DEFAULT_USERNAME"))
				.password(encoder.encode(System.getenv("DEFAULT_PASSWORD"))).roles("USER").build();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(user);
		return manager;
	}
}
