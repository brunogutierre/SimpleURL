package io.github.brunogutierre.simpleurl.link.dto;

import java.net.URI;
import java.time.Instant;

import io.github.brunogutierre.simpleurl.config.SimpleUrlProperties;
import io.github.brunogutierre.simpleurl.link.ShortLink;
import org.jspecify.annotations.Nullable;

/**
 * Public representation of a short link.
 */
public record LinkResponse(String code, URI shortUrl, String targetUrl, Instant createdAt,
		@Nullable Instant expiresAt) {

	public static LinkResponse from(ShortLink link, SimpleUrlProperties properties) {
		return new LinkResponse(link.getCode(), properties.shortUrlFor(link.getCode()), link.getTargetUrl(),
				link.getCreatedAt(), link.getExpiresAt());
	}

}
