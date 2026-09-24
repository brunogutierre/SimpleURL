package io.github.brunogutierre.simpleurl.link;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class HttpUrlValidatorTest {

	private final HttpUrlValidator validator = new HttpUrlValidator();

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { "http://example.com", "https://example.com/path?q=1#top", "HTTPS://EXAMPLE.COM",
			"https://localhost:8080/a" })
	void acceptsHttpUrls(String url) {
		assertThat(validator.isValid(url, null)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "example.com", "ftp://example.com", "javascript:alert(1)", "https://",
			"https:///path", "http://exa mple.com", "mailto:someone@example.com" })
	void rejectsNonHttpOrMalformedUrls(String url) {
		assertThat(validator.isValid(url, null)).isFalse();
	}

	@ParameterizedTest
	@ValueSource(ints = { 2048, 2049 })
	void limitsUrlLength(int length) {
		String prefix = "https://example.com/";
		String url = prefix + "a".repeat(length - prefix.length());

		assertThat(validator.isValid(url, null)).isEqualTo(length <= ShortLink.MAX_TARGET_URL_LENGTH);
	}

}
