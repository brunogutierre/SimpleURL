package io.github.brunogutierre.simpleurl.click;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

/**
 * Thread-safe in-memory store standing in for the {@code link_click} table,
 * grouped by link like the table's (short_link_id, clicked_at) index.
 */
@Repository
class InMemoryClickRepository implements ClickRepository {

	private final Map<Long, Queue<Click>> clicksByLinkId = new ConcurrentHashMap<>();

	private final AtomicLong idSequence = new AtomicLong();

	@Override
	public void save(Click click) {
		click.assignId(idSequence.incrementAndGet());
		clicksByLinkId.computeIfAbsent(click.getShortLink().getId(), id -> new ConcurrentLinkedQueue<>()).add(click);
	}

	@Override
	public List<Click> findByShortLinkId(long shortLinkId) {
		return List.copyOf(clicksByLinkId.getOrDefault(shortLinkId, new ConcurrentLinkedQueue<>()));
	}

	@Override
	public void deleteByShortLinkId(long shortLinkId) {
		clicksByLinkId.remove(shortLinkId);
	}

}
