package io.github.brunogutierre.simpleurl.link;

import java.time.Instant;

/**
 * Builds persisted-looking links (with an id) for tests outside this package.
 */
public final class ShortLinkFixtures {

	private ShortLinkFixtures() {
	}

	public static ShortLink persistedLink(long id, String code) {
		var link = ShortLink.create(code, "https://example.com/" + code, Instant.EPOCH);
		link.assignId(id);
		return link;
	}

}
