package io.github.brunogutierre.simpleurl.link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * No short link exists for the requested code. Rendered as a 404 Problem Detail.
 */
public class LinkNotFoundException extends ErrorResponseException {

	public LinkNotFoundException(String code) {
		super(HttpStatus.NOT_FOUND, problem("No short link found for code '%s'".formatted(code)), null);
	}

	private static ProblemDetail problem(String detail) {
		var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
		problem.setTitle("Link not found");
		return problem;
	}

}
