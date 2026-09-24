package io.github.brunogutierre.simpleurl.click;

import java.time.Clock;

import io.github.brunogutierre.simpleurl.link.ShortLink;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * Records link visits.
 */
@Service
public class ClickService {

	private final ClickRepository repository;

	private final Clock clock;

	public ClickService(ClickRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	public void record(ShortLink link, @Nullable String referer, @Nullable String userAgent) {
		repository.save(Click.of(link, clock.instant(), referer, userAgent));
	}

}
