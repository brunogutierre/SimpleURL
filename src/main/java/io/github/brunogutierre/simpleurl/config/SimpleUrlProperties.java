package io.github.brunogutierre.simpleurl.config;

import java.net.URI;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Application settings bound from the {@code simpleurl.*} properties.
 *
 * @param baseUrl public address of the service, used to build short URLs
 */
@Validated
@ConfigurationProperties("simpleurl")
public record SimpleUrlProperties(@NotNull @DefaultValue("http://localhost:8080") URI baseUrl) {

	/**
	 * Builds the public short URL for the given code.
	 */
	public URI shortUrlFor(String code) {
		String base = baseUrl.toString().replaceAll("/+$", "");
		return URI.create(base + "/" + code);
	}

}
