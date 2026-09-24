package io.github.brunogutierre.simpleurl.link;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * The requested expiration is not in the future. Rendered as a 400 Problem Detail
 * in the same shape as Bean Validation errors.
 */
public class InvalidExpirationException extends ErrorResponseException {

	public InvalidExpirationException() {
		super(HttpStatus.BAD_REQUEST, problem(), null);
	}

	private static ProblemDetail problem() {
		var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
		problem.setProperty("errors", Map.of("expiresAt", "must be in the future"));
		return problem;
	}

}
