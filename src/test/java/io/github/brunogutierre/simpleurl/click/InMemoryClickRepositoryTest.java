package io.github.brunogutierre.simpleurl.click;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static io.github.brunogutierre.simpleurl.link.ShortLinkFixtures.persistedLink;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryClickRepositoryTest {

	private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

	private final InMemoryClickRepository repository = new InMemoryClickRepository();

	private final Click first = Click.of(persistedLink(1, "link001"), NOW, null, null);

	private final Click second = Click.of(persistedLink(1, "link001"), NOW, null, null);

	private final Click other = Click.of(persistedLink(2, "link002"), NOW, null, null);

	@Test
	void savesClicksWithIdsGroupedByLink() {
		repository.save(first);
		repository.save(second);
		repository.save(other);

		assertThat(repository.findByShortLinkId(1)).containsExactly(first, second);
		assertThat(repository.findByShortLinkId(2)).containsExactly(other);
		assertThat(repository.findByShortLinkId(3)).isEmpty();
		assertThat(first.getId()).isEqualTo(1L);
	}

	@Test
	void deletesOnlyClicksOfGivenLink() {
		repository.save(first);
		repository.save(other);

		repository.deleteByShortLinkId(1);

		assertThat(repository.findByShortLinkId(1)).isEmpty();
		assertThat(repository.findByShortLinkId(2)).containsExactly(other);
	}

	@Test
	void truncatesLongHeaders() {
		var click = Click.of(persistedLink(1, "link001"), NOW, "r".repeat(3000), "u".repeat(600));

		assertThat(click.getReferer()).hasSize(Click.MAX_REFERER_LENGTH);
		assertThat(click.getUserAgent()).hasSize(Click.MAX_USER_AGENT_LENGTH);
	}

}
