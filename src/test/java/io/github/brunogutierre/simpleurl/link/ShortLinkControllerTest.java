package io.github.brunogutierre.simpleurl.link;

import java.time.Instant;

import io.github.brunogutierre.simpleurl.config.SimpleUrlProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest(ShortLinkController.class)
@EnableConfigurationProperties(SimpleUrlProperties.class)
@TestPropertySource(properties = "simpleurl.base-url=https://sho.rt")
class ShortLinkControllerTest {

	private static final String URL = "https://example.com/long";

	private static final ShortLink LINK = ShortLink.create("abc1234", URL, Instant.parse("2026-01-01T10:00:00Z"));

	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	ShortLinkService service;

	@Test
	void createsLink() {
		given(service.create(URL)).willReturn(LINK);

		assertThat(post("{\"url\":\"" + URL + "\"}")).hasStatus(HttpStatus.CREATED)
			.hasHeader("Location", "http://localhost/api/links/abc1234")
			.bodyJson()
			.isEqualTo("""
					{"code":"abc1234","shortUrl":"https://sho.rt/abc1234","targetUrl":"%s",
					 "createdAt":"2026-01-01T10:00:00Z"}""".formatted(URL));
	}

	@ParameterizedTest
	@ValueSource(strings = { "{}", "{\"url\":\"\"}", "{\"url\":\"ftp://example.com\"}" })
	void rejectsInvalidUrlWithFieldErrors(String body) {
		assertThat(post(body)).hasStatus(HttpStatus.BAD_REQUEST)
			.hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
			.bodyJson()
			.hasPathSatisfying("$.errors.url", url -> assertThat(url).isNotNull());
	}

	@Test
	void rejectsMalformedJson() {
		assertThat(post("{not json")).hasStatus(HttpStatus.BAD_REQUEST)
			.hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
	}

	@Test
	void getsLink() {
		given(service.get("abc1234")).willReturn(LINK);

		assertThat(mvc.get().uri("/api/links/abc1234")).hasStatusOk()
			.bodyJson()
			.extractingPath("$.targetUrl")
			.isEqualTo(URL);
	}

	@Test
	void deletesLink() {
		assertThat(mvc.delete().uri("/api/links/abc1234")).hasStatus(HttpStatus.NO_CONTENT);
		then(service).should().delete("abc1234");
	}

	@Test
	void returnsNotFoundProblemForUnknownCode() {
		given(service.get("missing")).willThrow(new LinkNotFoundException("missing"));
		willThrow(new LinkNotFoundException("missing")).given(service).delete("missing");

		assertNotFoundProblem(mvc.get().uri("/api/links/missing"));
		assertNotFoundProblem(mvc.delete().uri("/api/links/missing"));
	}

	private static void assertNotFoundProblem(MockMvcTester.MockMvcRequestBuilder request) {
		assertThat(request).hasStatus(HttpStatus.NOT_FOUND)
			.hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
			.bodyJson()
			.extractingPath("$.title")
			.isEqualTo("Link not found");
	}

	private MockMvcTester.MockMvcRequestBuilder post(String body) {
		return mvc.post().uri("/api/links").contentType(MediaType.APPLICATION_JSON).content(body);
	}

}
