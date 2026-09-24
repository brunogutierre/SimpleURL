package io.github.brunogutierre.simpleurl.link.dto;

import io.github.brunogutierre.simpleurl.link.HttpUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body to shorten a URL.
 */
public record CreateLinkRequest(
		@Schema(description = "URL to shorten", example = "https://example.com/some/long/path")
		@NotBlank @HttpUrl String url) {
}
