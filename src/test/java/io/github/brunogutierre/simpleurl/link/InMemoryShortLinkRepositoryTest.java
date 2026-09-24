package io.github.brunogutierre.simpleurl.link;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryShortLinkRepositoryTest {

	private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

	private final InMemoryShortLinkRepository repository = new InMemoryShortLinkRepository();

	@Test
	void savesAndFindsLinkByCodeWithGeneratedId() {
		var link = link("abc1234");

		assertThat(repository.saveIfCodeAbsent(link)).isTrue();
		assertThat(link.getId()).isEqualTo(1L);
		assertThat(repository.findByCode("abc1234")).containsSame(link);
		assertThat(repository.findByCode("missing")).isEmpty();
	}

	@Test
	void rejectsDuplicateCodeAndKeepsOriginal() {
		var original = link("abc1234");
		repository.saveIfCodeAbsent(original);

		assertThat(repository.saveIfCodeAbsent(link("abc1234"))).isFalse();
		assertThat(repository.findByCode("abc1234")).containsSame(original);
	}

	@Test
	void deletesLink() {
		var link = link("abc1234");
		repository.saveIfCodeAbsent(link);

		repository.delete(link);

		assertThat(repository.findByCode("abc1234")).isEmpty();
	}

	@Test
	void storesExactlyOneLinkWhenSameCodeIsSavedConcurrently() throws Exception {
		Callable<Boolean> save = () -> repository.saveIfCodeAbsent(link("race123"));

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			var results = executor.invokeAll(IntStream.range(0, 100).mapToObj(i -> save).toList());
			long stored = 0;
			for (var result : results) {
				stored += result.get() ? 1 : 0;
			}
			assertThat(stored).isEqualTo(1);
		}
	}

	private static ShortLink link(String code) {
		return ShortLink.create(code, "https://example.com", NOW);
	}

}
