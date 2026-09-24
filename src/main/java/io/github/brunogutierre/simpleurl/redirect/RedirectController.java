package io.github.brunogutierre.simpleurl.redirect;

import java.net.URI;

import io.github.brunogutierre.simpleurl.link.ShortLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public entry point: sends visitors from a short code to the target URL.
 * The path pattern only matches short codes, so it never shadows other routes
 * such as {@code /swagger-ui.html} or {@code /favicon.ico}.
 */
@Tag(name = "Redirect", description = "Follow short links")
@RestController
class RedirectController {

	private final ShortLinkService linkService;

	RedirectController(ShortLinkService linkService) {
		this.linkService = linkService;
	}

	/**
	 * Uses 302 instead of 301: browsers cache permanent redirects, which would hide
	 * later visits from the service and ignore deleted links.
	 */
	@Operation(summary = "Redirect to the target URL of a short link")
	@GetMapping("/{code:[0-9A-Za-z]{7}}")
	ResponseEntity<Void> redirect(@PathVariable String code) {
		var link = linkService.get(code);
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link.getTargetUrl())).build();
	}

}
