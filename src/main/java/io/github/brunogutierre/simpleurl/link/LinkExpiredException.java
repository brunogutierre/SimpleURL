package io.github.brunogutierre.simpleurl.link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * The link exists but has expired. Rendered as a 410 Gone Problem Detail, which tells
 * clients the resource is permanently unavailable rather than unknown.
 */
public class LinkExpiredException extends ErrorResponseException {

	public LinkExpiredException(String code) {
		super(HttpStatus.GONE, problem("Short link '%s' has expired".formatted(code)), null);
	}

	private static ProblemDetail problem(String detail) {
		var problem = ProblemDetail.forStatusAndDetail(HttpStatus.GONE, detail);
		problem.setTitle("Link expired");
		return problem;
	}

}
