package io.github.brunogutierre.simpleurl.link;

import java.util.Optional;

/**
 * Persistence port for short links. Implementations must keep codes unique.
 */
public interface ShortLinkRepository {

	/**
	 * Stores the link unless its code is already taken.
	 * @return {@code true} if stored, {@code false} if the code already exists
	 */
	boolean saveIfCodeAbsent(ShortLink link);

	Optional<ShortLink> findByCode(String code);

	void delete(ShortLink link);

}
