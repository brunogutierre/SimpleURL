package io.github.brunogutierre.simpleurl.link;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortLinkServiceTest {

	private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

	private static final String URL = "https://example.com/some/long/path";

	private final ShortLinkRepository repository = new InMemoryShortLinkRepository();

	private final List<Object> publishedEvents = new ArrayList<>();

	@Test
	void createsLinkWithGeneratedCodeAndCurrentTime() {
		var service = serviceGenerating("abc1234");

		var link = service.create(URL, null);

		assertThat(link.getCode()).isEqualTo("abc1234");
		assertThat(link.getTargetUrl()).isEqualTo(URL);
		assertThat(link.getCreatedAt()).isEqualTo(NOW);
		assertThat(link.getExpiresAt()).isNull();
		assertThat(service.get("abc1234")).isSameAs(link);
	}

	@Test
	void createsLinkWithFutureExpiration() {
		var expiresAt = NOW.plusSeconds(1);

		assertThat(serviceGenerating("abc1234").create(URL, expiresAt).getExpiresAt()).isEqualTo(expiresAt);
	}

	@ParameterizedTest
	@ValueSource(longs = { 0, -1 })
	void rejectsExpirationThatIsNotInTheFuture(long secondsFromNow) {
		var service = serviceGenerating("abc1234");

		assertThatThrownBy(() -> service.create(URL, NOW.plusSeconds(secondsFromNow)))
			.isInstanceOf(InvalidExpirationException.class);
		assertThat(repository.findByCode("abc1234")).isEmpty();
	}

	@Test
	void retriesWhenGeneratedCodeIsTaken() {
		var service = serviceGenerating("taken01", "taken01", "free001");
		service.create(URL, null);

		assertThat(service.create(URL, null).getCode()).isEqualTo("free001");
	}

	@Test
	void failsAfterMaxAttemptsWhenEveryCodeIsTaken() {
		var service = serviceGenerating("taken01");
		service.create(URL, null);

		assertThatThrownBy(() -> service.create(URL, null)).isInstanceOf(CodeGenerationFailedException.class)
			.hasMessageContaining("after 5 attempts");
	}

	@Test
	void resolvesActiveLinks() {
		var service = serviceGenerating("unused1");
		repository.saveIfCodeAbsent(ShortLink.create("forever", URL, NOW));
		repository.saveIfCodeAbsent(ShortLink.create("later01", URL, NOW, NOW.plusSeconds(1)));

		assertThat(service.resolve("forever").getCode()).isEqualTo("forever");
		assertThat(service.resolve("later01").getCode()).isEqualTo("later01");
	}

	@Test
	void refusesToResolveExpiredLink() {
		var service = serviceGenerating("unused1");
		repository.saveIfCodeAbsent(ShortLink.create("expired", URL, NOW.minusSeconds(60), NOW));

		assertThatThrownBy(() -> service.resolve("expired")).isInstanceOf(LinkExpiredException.class);
		assertThat(service.get("expired").getCode()).isEqualTo("expired");
	}

	@Test
	void deletesLinkAndPublishesEvent() {
		var service = serviceGenerating("abc1234");
		var link = service.create(URL, null);

		service.delete("abc1234");

		assertThat(repository.findByCode("abc1234")).isEmpty();
		assertThat(publishedEvents).containsExactly(new ShortLinkDeletedEvent(link.getId(), "abc1234"));
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
		return new ShortLinkService(repository, generator, Clock.fixed(NOW, ZoneOffset.UTC), publishedEvents::add);
	}

}
