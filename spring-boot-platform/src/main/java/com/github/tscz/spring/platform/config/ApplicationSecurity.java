package com.github.tscz.spring.platform.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.tscz.spring.platform.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http//
				.csrf().disable()
				// enable stateless session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// handle an authorized attempts
				.exceptionHandling().and()
				// Add a filter to validate the tokens with every request
				.addFilterAfter(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
				// authorization requests config
				.authorizeHttpRequests((authz) -> authz//
						.requestMatchers("/token/**", "/error/**").permitAll()
						// Any other request must be authenticated
						.anyRequest().authenticated());

		return http.build();
	}

	@Autowired
	private DataSource dataSource;

	@Bean
	public UserDetailsManager users() {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
		manager.setDataSource(dataSource);

		return manager;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public JwtFilter authenticationTokenFilterBean() throws Exception {
		return new JwtFilter();
	}

}
