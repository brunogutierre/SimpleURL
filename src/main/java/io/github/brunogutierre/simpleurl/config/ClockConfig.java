package io.github.brunogutierre.simpleurl.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Single source of the current time, so time-dependent logic (such as link
 * expiration) can be tested with a fixed clock.
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfig {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

}
