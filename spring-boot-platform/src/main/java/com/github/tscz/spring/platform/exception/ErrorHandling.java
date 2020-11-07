package com.github.tscz.spring.platform.exception;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

/**
 * Error Handling for Spring Boot App:
 * <ul>
 * <li>Overrides default error controller from spring, because Exceptions thrown
 * in {@link javax.servlet.Filter} are not handled in
 * {@link org.springframework.web.bind.annotation.ExceptionHandler}.</li>
 * 
 * <li>Add ExceptionHandlers, which are implementing
 * <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a> for standardized
 * application/problem+json rest responses.</li>
 * </ul>
 */
@RestController
@ControllerAdvice
public class ErrorHandling implements ErrorController, ProblemHandling, SecurityAdviceTrait {

	@RequestMapping(value = "/error")
	Problem error(HttpServletRequest request, HttpServletResponse response) throws Throwable {

		Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

		if (throwable instanceof ThrowableProblem)
			throw throwable;
		else
			throw toProblem(throwable);

	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}