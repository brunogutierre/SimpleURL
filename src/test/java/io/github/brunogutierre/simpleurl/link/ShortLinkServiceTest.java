package io.github.brunogutierre.simpleurl.link;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortLinkServiceTest {

	private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

	private static final String URL = "https://example.com/some/long/path";

	private final ShortLinkRepository repository = new InMemoryShortLinkRepository();

	@Test
	void createsLinkWithGeneratedCodeAndCurrentTime() {
		var service = serviceGenerating("abc1234");

		var link = service.create(URL);

		assertThat(link.getCode()).isEqualTo("abc1234");
		assertThat(link.getTargetUrl()).isEqualTo(URL);
		assertThat(link.getCreatedAt()).isEqualTo(NOW);
		assertThat(service.get("abc1234")).isSameAs(link);
	}

	@Test
	void retriesWhenGeneratedCodeIsTaken() {
		var service = serviceGenerating("taken01", "taken01", "free001");
		service.create(URL);

		assertThat(service.create(URL).getCode()).isEqualTo("free001");
	}

	@Test
	void failsAfterMaxAttemptsWhenEveryCodeIsTaken() {
		var service = serviceGenerating("taken01");
		service.create(URL);

		assertThatThrownBy(() -> service.create(URL)).isInstanceOf(CodeGenerationFailedException.class)
			.hasMessageContaining("after 5 attempts");
	}

	@Test
	void deletesLink() {
		var service = serviceGenerating("abc1234");
		service.create(URL);

		service.delete("abc1234");

		assertThat(repository.findByCode("abc1234")).isEmpty();
	}

	@Test
	void throwsNotFoundForUnknownCode() {
		var service = serviceGenerating("abc1234");

		assertThatThrownBy(() -> service.get("missing")).isInstanceOfSatisfying(LinkNotFoundException.class,
				ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
		assertThatThrownBy(() -> service.delete("missing")).isInstanceOf(LinkNotFoundException.class);
	}

	/** Returns the given codes in order, repeating the last one forever. */
	private ShortLinkService serviceGenerating(String... codes) {
		Iterator<String> iterator = List.of(codes).iterator();
		String last = codes[codes.length - 1];
		CodeGenerator generator = () -> iterator.hasNext() ? iterator.next() : last;
		return new ShortLinkService(repository, generator, Clock.fixed(NOW, ZoneOffset.UTC));
	}

}
