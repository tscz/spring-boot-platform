package com.github.tscz.spring.platform.exception;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class Exceptions {

	public static ErrorResponseException constraintViolation(String propertyName, String propertyValue) {

		var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setProperty("violations", Stream.of(Map.of("field", propertyName, "message", propertyValue)).toArray());
		problem.setTitle("Constraint Violation");

		return new ErrorResponseException(HttpStatus.BAD_REQUEST, problem, null);

	}
}
