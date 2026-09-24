package io.github.brunogutierre.simpleurl.link.dto;

import java.time.Instant;

import io.github.brunogutierre.simpleurl.link.HttpUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

/**
 * Request body to shorten a URL.
 */
public record CreateLinkRequest(
		@Schema(description = "URL to shorten", example = "https://example.com/some/long/path")
		@NotBlank @HttpUrl String url,
		@Schema(description = "When the link stops working (ISO-8601, must be in the future); omit to never expire",
				example = "2030-01-01T00:00:00Z")
		@Nullable Instant expiresAt) {
}
