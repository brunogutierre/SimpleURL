package io.github.brunogutierre.simpleurl.click;

import java.time.Instant;
import java.util.Objects;

import io.github.brunogutierre.simpleurl.link.ShortLink;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

/**
 * One visit to a short link. Maps the {@code link_click} table.
 */
@Entity
@Table(name = "link_click")
public class Click {

	static final int MAX_REFERER_LENGTH = 2048;

	static final int MAX_USER_AGENT_LENGTH = 512;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "short_link_id", nullable = false)
	private ShortLink shortLink;

	@Column(name = "clicked_at", nullable = false)
	private Instant clickedAt;

	@Column(name = "referer", length = MAX_REFERER_LENGTH)
	private @Nullable String referer;

	@Column(name = "user_agent", length = MAX_USER_AGENT_LENGTH)
	private @Nullable String userAgent;

	/** Required by JPA. */
	protected Click() {
	}

	private Click(ShortLink shortLink, Instant clickedAt, @Nullable String referer, @Nullable String userAgent) {
		this.shortLink = Objects.requireNonNull(shortLink, "shortLink");
		this.clickedAt = Objects.requireNonNull(clickedAt, "clickedAt");
		this.referer = truncate(referer, MAX_REFERER_LENGTH);
		this.userAgent = truncate(userAgent, MAX_USER_AGENT_LENGTH);
	}

	/** Creates a click, truncating client-provided headers to the column sizes. */
	public static Click of(ShortLink shortLink, Instant clickedAt, @Nullable String referer,
			@Nullable String userAgent) {
		return new Click(shortLink, clickedAt, referer, userAgent);
	}

	private static @Nullable String truncate(@Nullable String value, int maxLength) {
		return (value == null || value.length() <= maxLength) ? value : value.substring(0, maxLength);
	}

	public Long getId() {
		return id;
	}

	/** Called by repositories that generate identifiers themselves. */
	void assignId(long id) {
		this.id = id;
	}

	public ShortLink getShortLink() {
		return shortLink;
	}

	public Instant getClickedAt() {
		return clickedAt;
	}

	public @Nullable String getReferer() {
		return referer;
	}

	public @Nullable String getUserAgent() {
		return userAgent;
	}

}
