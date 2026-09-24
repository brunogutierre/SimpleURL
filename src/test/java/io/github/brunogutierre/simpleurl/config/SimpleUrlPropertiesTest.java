package io.github.brunogutierre.simpleurl.config;

import java.net.URI;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleUrlPropertiesTest {

	@ParameterizedTest
	@ValueSource(strings = { "https://sho.rt", "https://sho.rt/", "https://sho.rt//" })
	void buildsShortUrlWithSingleSlash(String baseUrl) {
		var properties = new SimpleUrlProperties(URI.create(baseUrl));

		assertThat(properties.shortUrlFor("aB3dE5f")).isEqualTo(URI.create("https://sho.rt/aB3dE5f"));
	}

}
