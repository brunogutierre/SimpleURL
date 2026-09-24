package io.github.brunogutierre.simpleurl.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsTest {

	@Autowired
	MockMvcTester mvc;

	@Test
	void exposesOpenApiDocumentWithApiMetadata() {
		assertThat(mvc.get().uri("/v3/api-docs"))
			.hasStatusOk()
			.bodyJson()
			.extractingPath("$.info.title")
			.isEqualTo("SimpleURL API");
	}

	@Test
	void servesSwaggerUi() {
		assertThat(mvc.get().uri("/swagger-ui/index.html")).hasStatusOk();
	}

}
