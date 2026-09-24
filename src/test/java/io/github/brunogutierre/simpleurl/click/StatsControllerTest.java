package io.github.brunogutierre.simpleurl.click;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import io.github.brunogutierre.simpleurl.click.dto.DailyClicks;
import io.github.brunogutierre.simpleurl.click.dto.LinkStatsResponse;
import io.github.brunogutierre.simpleurl.link.LinkNotFoundException;
import io.github.brunogutierre.simpleurl.link.ShortLink;
import io.github.brunogutierre.simpleurl.link.ShortLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	ShortLinkService linkService;

	@MockitoBean
	ClickService clickService;

	@Test
	void returnsLinkStatistics() {
		var link = ShortLink.create("abc1234", "https://example.com", Instant.EPOCH);
		given(linkService.get("abc1234")).willReturn(link);
		given(clickService.statsFor(link)).willReturn(new LinkStatsResponse("abc1234", 2,
				Instant.parse("2026-01-02T10:00:00Z"), List.of(new DailyClicks(LocalDate.parse("2026-01-02"), 2))));

		assertThat(mvc.get().uri("/api/links/abc1234/stats")).hasStatusOk().bodyJson().isEqualTo("""
				{"code":"abc1234","totalClicks":2,"lastClickAt":"2026-01-02T10:00:00Z",
				 "clicksPerDay":[{"date":"2026-01-02","clicks":2}]}""");
	}

	@Test
	void returnsNotFoundForUnknownCode() {
		given(linkService.get("missing")).willThrow(new LinkNotFoundException("missing"));

		assertThat(mvc.get().uri("/api/links/missing/stats")).hasStatus(HttpStatus.NOT_FOUND);
	}

}
