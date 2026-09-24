package io.github.brunogutierre.simpleurl.click;

import java.util.List;

/**
 * Persistence port for clicks.
 */
public interface ClickRepository {

	void save(Click click);

	List<Click> findByShortLinkId(long shortLinkId);

	void deleteByShortLinkId(long shortLinkId);

}
