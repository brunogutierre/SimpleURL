package io.github.brunogutierre.simpleurl.link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Every generated code collided with an existing one. Practically impossible with
 * 7-character Base62 codes unless the code space is nearly exhausted.
 */
public class CodeGenerationFailedException extends ErrorResponseException {

	public CodeGenerationFailedException(int attempts) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Could not generate a unique short code after %d attempts".formatted(attempts)), null);
	}

}
