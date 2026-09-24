package io.github.brunogutierre.simpleurl.redirect;

import java.time.Instant;

import io.github.brunogutierre.simpleurl.click.ClickService;
import io.github.brunogutierre.simpleurl.link.LinkExpiredException;
import io.github.brunogutierre.simpleurl.link.LinkNotFoundException;
import io.github.brunogutierre.simpleurl.link.ShortLink;
import io.github.brunogutierre.simpleurl.link.ShortLinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	ShortLinkService linkService;

	@MockitoBean
	ClickService clickService;

	@Test
	void redirectsToTargetUrlAndRecordsClick() {
		var link = ShortLink.create("abc1234", "https://example.com/long?q=1", Instant.EPOCH);
		given(linkService.resolve("abc1234")).willReturn(link);

		assertThat(mvc.get().uri("/abc1234").header("Referer", "https://ref.example").header("User-Agent", "curl/8"))
			.hasStatus(HttpStatus.FOUND)
			.hasRedirectedUrl("https://example.com/long?q=1");
		then(clickService).should().record(link, "https://ref.example", "curl/8");
	}

	@Test
	void returnsNotFoundForUnknownCode() {
		given(linkService.resolve("zzzzzzz")).willThrow(new LinkNotFoundException("zzzzzzz"));

		assertThat(mvc.get().uri("/zzzzzzz")).hasStatus(HttpStatus.NOT_FOUND);
		then(clickService).shouldHaveNoInteractions();
	}

	@Test
	void returnsGoneForExpiredLink() {
		given(linkService.resolve("abc1234")).willThrow(new LinkExpiredException("abc1234"));

		assertThat(mvc.get().uri("/abc1234")).hasStatus(HttpStatus.GONE)
			.bodyJson()
			.extractingPath("$.title")
			.isEqualTo("Link expired");
		then(clickService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@ValueSource(strings = { "/abc123", "/abc12345", "/abc-123", "/favicon.ico" })
	void ignoresPathsThatAreNotShortCodes(String path) {
		assertThat(mvc.get().uri(path)).hasStatus(HttpStatus.NOT_FOUND);
		then(linkService).shouldHaveNoInteractions();
	}

}
