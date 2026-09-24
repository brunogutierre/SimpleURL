package io.github.brunogutierre.simpleurl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the whole application: create, follow, inspect and delete a link.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LinkLifecycleIntegrationTest {

	@Autowired
	MockMvcTester mvc;

	@Test
	void createRedirectInspectAndDeleteLink() {
		var created = mvc.post()
			.uri("/api/links")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"url\":\"https://example.com/docs\"}")
			.exchange();
		assertThat(created).hasStatus(HttpStatus.CREATED);
		String code = created.getMvcResult().getResponse().getHeader("Location").replaceAll(".*/", "");

		assertThat(mvc.get().uri("/{code}", code)).hasStatus(HttpStatus.FOUND)
			.hasRedirectedUrl("https://example.com/docs");
		assertThat(mvc.get().uri("/api/links/{code}", code)).hasStatusOk()
			.bodyJson()
			.extractingPath("$.shortUrl")
			.isEqualTo("http://localhost:8080/" + code);
		assertThat(mvc.get().uri("/api/links/{code}/stats", code)).hasStatusOk()
			.bodyJson()
			.extractingPath("$.totalClicks")
			.isEqualTo(1);

		assertThat(mvc.delete().uri("/api/links/{code}", code)).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(mvc.get().uri("/{code}", code)).hasStatus(HttpStatus.NOT_FOUND);
		assertThat(mvc.get().uri("/api/links/{code}/stats", code)).hasStatus(HttpStatus.NOT_FOUND);
	}

}
