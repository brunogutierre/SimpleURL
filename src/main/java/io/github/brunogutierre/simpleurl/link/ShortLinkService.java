package io.github.brunogutierre.simpleurl.link;

import java.time.Clock;

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

	public ShortLinkService(ShortLinkRepository repository, CodeGenerator codeGenerator, Clock clock) {
		this.repository = repository;
		this.codeGenerator = codeGenerator;
		this.clock = clock;
	}

	/**
	 * Creates a link with a new unique code, retrying when a generated code is taken.
	 * @throws CodeGenerationFailedException if no unique code is found
	 */
	public ShortLink create(String targetUrl) {
		var now = clock.instant();
		for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
			var link = ShortLink.create(codeGenerator.generate(), targetUrl, now);
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
	 * @throws LinkNotFoundException if no link has the given code
	 */
	public void delete(String code) {
		repository.delete(get(code));
	}

}
