package io.github.brunogutierre.simpleurl.link;

import io.github.brunogutierre.simpleurl.config.SimpleUrlProperties;
import io.github.brunogutierre.simpleurl.link.dto.CreateLinkRequest;
import io.github.brunogutierre.simpleurl.link.dto.LinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Manages short links.
 */
@Tag(name = "Links", description = "Create, read and delete short links")
@RestController
@RequestMapping("/api/links")
class ShortLinkController {

	private final ShortLinkService service;

	private final SimpleUrlProperties properties;

	ShortLinkController(ShortLinkService service, SimpleUrlProperties properties) {
		this.service = service;
		this.properties = properties;
	}

	@Operation(summary = "Shorten a URL")
	@PostMapping
	ResponseEntity<LinkResponse> create(@Valid @RequestBody CreateLinkRequest request) {
		var link = service.create(request.url(), request.expiresAt());
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{code}")
			.buildAndExpand(link.getCode())
			.toUri();
		return ResponseEntity.created(location).body(LinkResponse.from(link, properties));
	}

	@Operation(summary = "Get a short link")
	@GetMapping("/{code}")
	LinkResponse get(@PathVariable String code) {
		return LinkResponse.from(service.get(code), properties);
	}

	@Operation(summary = "Delete a short link")
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@PathVariable String code) {
		service.delete(code);
	}

}
