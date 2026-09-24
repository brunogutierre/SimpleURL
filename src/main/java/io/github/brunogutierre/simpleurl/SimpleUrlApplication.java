package io.github.brunogutierre.simpleurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SimpleUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleUrlApplication.class, args);
	}

}
