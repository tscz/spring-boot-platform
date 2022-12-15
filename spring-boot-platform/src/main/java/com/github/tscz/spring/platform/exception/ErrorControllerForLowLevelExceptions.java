package com.github.tscz.spring.platform.exception;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponseException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Error Handling for Spring Boot App:
 * <ul>
 * <li>Overrides default error controller from spring, because Exceptions thrown
 * in {@link jakarta.servlet.Filter} are not handled in
 * {@link org.springframework.web.bind.annotation.ExceptionHandler}.</li>
 * 
 * <li>Add ExceptionHandlers, which are implementing
 * <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a> for standardized
 * application/problem+json rest responses.</li>
 * </ul>
 */
@Controller
public class ErrorControllerForLowLevelExceptions extends BasicErrorController {

	public ErrorControllerForLowLevelExceptions(ServerProperties serverProperties) {
		super(new DefaultErrorAttributes(), serverProperties.getError());
	}

	@Override
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {

		Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

		if (throwable instanceof ErrorResponseException e) {
			throw e;
		}

		return super.error(request);
	}

}
