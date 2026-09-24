package io.github.brunogutierre.simpleurl.redirect;

import java.time.Instant;

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

	@Test
	void redirectsToTargetUrl() {
		given(linkService.get("abc1234"))
			.willReturn(ShortLink.create("abc1234", "https://example.com/long?q=1", Instant.EPOCH));

		assertThat(mvc.get().uri("/abc1234")).hasStatus(HttpStatus.FOUND)
			.hasRedirectedUrl("https://example.com/long?q=1");
	}

	@Test
	void returnsNotFoundForUnknownCode() {
		given(linkService.get("zzzzzzz")).willThrow(new LinkNotFoundException("zzzzzzz"));

		assertThat(mvc.get().uri("/zzzzzzz")).hasStatus(HttpStatus.NOT_FOUND);
	}

	@ParameterizedTest
	@ValueSource(strings = { "/abc123", "/abc12345", "/abc-123", "/favicon.ico" })
	void ignoresPathsThatAreNotShortCodes(String path) {
		assertThat(mvc.get().uri(path)).hasStatus(HttpStatus.NOT_FOUND);
		then(linkService).shouldHaveNoInteractions();
	}

}
