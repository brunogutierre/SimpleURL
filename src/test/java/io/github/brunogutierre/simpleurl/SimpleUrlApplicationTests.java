package io.github.brunogutierre.simpleurl;

import java.net.URI;
import java.time.Clock;
import java.time.ZoneOffset;

import io.github.brunogutierre.simpleurl.config.SimpleUrlProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "simpleurl.base-url=https://sho.rt")
class SimpleUrlApplicationTests {

	@Autowired
	Clock clock;

	@Autowired
	SimpleUrlProperties properties;

	@Test
	void usesUtcSystemClock() {
		assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
	}

	@Test
	void bindsApplicationProperties() {
		assertThat(properties.baseUrl()).isEqualTo(URI.create("https://sho.rt"));
	}

}
