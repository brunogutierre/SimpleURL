package io.github.brunogutierre.simpleurl.click;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

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

}
