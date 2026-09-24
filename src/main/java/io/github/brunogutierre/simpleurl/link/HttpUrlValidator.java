package io.github.brunogutierre.simpleurl.link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates {@link HttpUrl} using {@link URI} parsing.
 */
public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

	private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		if (value.length() > ShortLink.MAX_TARGET_URL_LENGTH) {
			return false;
		}
		try {
			var uri = new URI(value);
			return uri.getScheme() != null && ALLOWED_SCHEMES.contains(uri.getScheme().toLowerCase())
					&& uri.getHost() != null;
		}
		catch (URISyntaxException ex) {
			return false;
		}
	}

}
