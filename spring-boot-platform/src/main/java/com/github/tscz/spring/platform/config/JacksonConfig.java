package com.github.tscz.spring.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

	@Autowired
	void configureObjectMapper(final ObjectMapper mapper) {
		mapper//
				.registerModule(new ProblemModule())//
				.registerModule(new ConstraintViolationProblemModule());
	}
}
