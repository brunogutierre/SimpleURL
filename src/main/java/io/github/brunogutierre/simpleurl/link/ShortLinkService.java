package io.github.brunogutierre.simpleurl.link;

import java.time.Clock;
import java.time.Instant;

import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Creates, finds and deletes short links.
 */
@Service
public class ShortLinkService {

	static final int MAX_CODE_ATTEMPTS = 5;

	private final ShortLinkRepository repository;

	private final CodeGenerator codeGenerator;

	private final Clock clock;

	private final ApplicationEventPublisher events;

	public ShortLinkService(ShortLinkRepository repository, CodeGenerator codeGenerator, Clock clock,
			ApplicationEventPublisher events) {
		this.repository = repository;
		this.codeGenerator = codeGenerator;
		this.clock = clock;
		this.events = events;
	}

	/**
	 * Creates a link with a new unique code, retrying when a generated code is taken.
	 * @param expiresAt when the link stops working, or {@code null} to never expire
	 * @throws InvalidExpirationException if {@code expiresAt} is not in the future
	 * @throws CodeGenerationFailedException if no unique code is found
	 */
	public ShortLink create(String targetUrl, @Nullable Instant expiresAt) {
		var now = clock.instant();
		if (expiresAt != null && !expiresAt.isAfter(now)) {
			throw new InvalidExpirationException();
		}
		for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
			var link = ShortLink.create(codeGenerator.generate(), targetUrl, now, expiresAt);
			if (repository.saveIfCodeAbsent(link)) {
				return link;
			}
		}
		throw new CodeGenerationFailedException(MAX_CODE_ATTEMPTS);
	}

	/**
	 * @throws LinkNotFoundException if no link has the given code
	 */
	public ShortLink get(String code) {
		return repository.findByCode(code).orElseThrow(() -> new LinkNotFoundException(code));
	}

	/**
	 * Finds the link a visitor should be redirected to.
	 * @throws LinkNotFoundException if no link has the given code
	 * @throws LinkExpiredException if the link has expired
	 */
	public ShortLink resolve(String code) {
		var link = get(code);
		if (link.isExpiredAt(clock.instant())) {
			throw new LinkExpiredException(code);
		}
		return link;
	}

	/**
	 * Deletes the link and publishes a {@link ShortLinkDeletedEvent}.
	 * @throws LinkNotFoundException if no link has the given code
	 */
	public void delete(String code) {
		var link = get(code);
		repository.delete(link);
		events.publishEvent(new ShortLinkDeletedEvent(link.getId(), link.getCode()));
	}

}
