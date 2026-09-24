package io.github.brunogutierre.simpleurl.link;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

/**
 * Thread-safe in-memory store standing in for the {@code short_link} table.
 * Code uniqueness is enforced atomically, like the table's unique constraint.
 */
@Repository
class InMemoryShortLinkRepository implements ShortLinkRepository {

	private final Map<String, ShortLink> linksByCode = new ConcurrentHashMap<>();

	private final AtomicLong idSequence = new AtomicLong();

	@Override
	public boolean saveIfCodeAbsent(ShortLink link) {
		if (linksByCode.containsKey(link.getCode())) {
			return false;
		}
		link.assignId(idSequence.incrementAndGet());
		return linksByCode.putIfAbsent(link.getCode(), link) == null;
	}

	@Override
	public Optional<ShortLink> findByCode(String code) {
		return Optional.ofNullable(linksByCode.get(code));
	}

	@Override
	public void delete(ShortLink link) {
		linksByCode.remove(link.getCode(), link);
	}

}
