package com.github.tscz.spring.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

	@Autowired
	void configureObjectMapper(final ObjectMapper mapper) {

	}
}
