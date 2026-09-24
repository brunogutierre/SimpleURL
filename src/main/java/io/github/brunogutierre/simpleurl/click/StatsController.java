package io.github.brunogutierre.simpleurl.click;

import io.github.brunogutierre.simpleurl.click.dto.LinkStatsResponse;
import io.github.brunogutierre.simpleurl.link.ShortLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes click statistics of short links.
 */
@Tag(name = "Statistics", description = "Click statistics of short links")
@RestController
class StatsController {

	private final ShortLinkService linkService;

	private final ClickService clickService;

	StatsController(ShortLinkService linkService, ClickService clickService) {
		this.linkService = linkService;
		this.clickService = clickService;
	}

	@Operation(summary = "Get click statistics of a short link")
	@GetMapping("/api/links/{code}/stats")
	LinkStatsResponse stats(@PathVariable String code) {
		return clickService.statsFor(linkService.get(code));
	}

}
