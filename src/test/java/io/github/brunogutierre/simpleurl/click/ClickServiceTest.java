package io.github.brunogutierre.simpleurl.click;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import io.github.brunogutierre.simpleurl.click.dto.DailyClicks;
import io.github.brunogutierre.simpleurl.link.ShortLinkDeletedEvent;
import org.junit.jupiter.api.Test;

import static io.github.brunogutierre.simpleurl.link.ShortLinkFixtures.persistedLink;
import static org.assertj.core.api.Assertions.assertThat;

class ClickServiceTest {

	private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

	private final ClickRepository repository = new InMemoryClickRepository();

	private final ClickService service = new ClickService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

	@Test
	void recordsClickWithCurrentTimeAndHeaders() {
		var link = persistedLink(1, "link001");

		service.record(link, "https://referrer.example", "Mozilla/5.0");

		assertThat(repository.findByShortLinkId(1)).singleElement().satisfies(click -> {
			assertThat(click.getShortLink()).isSameAs(link);
			assertThat(click.getClickedAt()).isEqualTo(NOW);
			assertThat(click.getReferer()).isEqualTo("https://referrer.example");
			assertThat(click.getUserAgent()).isEqualTo("Mozilla/5.0");
		});
	}

	@Test
	void deletesClicksWhenLinkIsDeleted() {
		service.record(persistedLink(1, "link001"), null, null);

		service.onLinkDeleted(new ShortLinkDeletedEvent(1, "link001"));

		assertThat(repository.findByShortLinkId(1)).isEmpty();
	}

	@Test
	void summarizesClicksPerUtcDay() {
		var link = persistedLink(1, "link001");
		for (var clickedAt : new String[] { "2026-01-02T23:59:59Z", "2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z" }) {
			repository.save(Click.of(link, Instant.parse(clickedAt), null, null));
		}

		var stats = service.statsFor(link);

		assertThat(stats.code()).isEqualTo("link001");
		assertThat(stats.totalClicks()).isEqualTo(3);
		assertThat(stats.lastClickAt()).isEqualTo("2026-01-02T23:59:59Z");
		assertThat(stats.clicksPerDay()).containsExactly(new DailyClicks(LocalDate.parse("2026-01-01"), 1),
				new DailyClicks(LocalDate.parse("2026-01-02"), 2));
	}

	@Test
	void summarizesLinkWithoutClicks() {
		var stats = service.statsFor(persistedLink(1, "link001"));

		assertThat(stats.totalClicks()).isZero();
		assertThat(stats.lastClickAt()).isNull();
		assertThat(stats.clicksPerDay()).isEmpty();
	}

}
