package io.github.brunogutierre.simpleurl.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadata shown in the generated OpenAPI document and Swagger UI.
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

	@Bean
	OpenAPI simpleUrlOpenApi() {
		return new OpenAPI().info(new Info()
			.title("SimpleURL API")
			.description("Shorten URLs, redirect visitors and track clicks.")
			.version("v1")
			.license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
	}

}
