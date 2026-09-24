package io.github.brunogutierre.simpleurl.click;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.github.brunogutierre.simpleurl.click.dto.DailyClicks;
import io.github.brunogutierre.simpleurl.click.dto.LinkStatsResponse;

import io.github.brunogutierre.simpleurl.link.ShortLink;
import io.github.brunogutierre.simpleurl.link.ShortLinkDeletedEvent;
import org.jspecify.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Records link visits, summarizes them and removes them when their link is deleted.
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

	/**
	 * Aggregates the link's clicks, grouping them by UTC day.
	 */
	public LinkStatsResponse statsFor(ShortLink link) {
		var clicks = repository.findByShortLinkId(link.getId());
		var clicksPerDay = clicks.stream()
			.collect(Collectors.groupingBy(click -> LocalDate.ofInstant(click.getClickedAt(), ZoneOffset.UTC),
					TreeMap::new, Collectors.counting()))
			.entrySet()
			.stream()
			.map(day -> new DailyClicks(day.getKey(), day.getValue()))
			.toList();
		Instant lastClickAt = clicks.stream().map(Click::getClickedAt).max(Comparator.naturalOrder()).orElse(null);
		return new LinkStatsResponse(link.getCode(), clicks.size(), lastClickAt, clicksPerDay);
	}

	/** Mirrors the ON DELETE CASCADE of the link_click foreign key. */
	@EventListener
	void onLinkDeleted(ShortLinkDeletedEvent event) {
		repository.deleteByShortLinkId(event.shortLinkId());
	}

}
