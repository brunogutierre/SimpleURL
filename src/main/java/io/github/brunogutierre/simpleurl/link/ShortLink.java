package io.github.brunogutierre.simpleurl.link;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

/**
 * A short code that points to a target URL, optionally until an expiration time.
 * Maps the {@code short_link} table.
 */
@Entity
@Table(name = "short_link")
public class ShortLink {

	public static final int MAX_CODE_LENGTH = 16;

	public static final int MAX_TARGET_URL_LENGTH = 2048;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, unique = true, length = MAX_CODE_LENGTH)
	private String code;

	@Column(name = "target_url", nullable = false, length = MAX_TARGET_URL_LENGTH)
	private String targetUrl;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "expires_at")
	private @Nullable Instant expiresAt;

	/** Required by JPA. */
	protected ShortLink() {
	}

	private ShortLink(String code, String targetUrl, Instant createdAt, @Nullable Instant expiresAt) {
		this.code = Objects.requireNonNull(code, "code");
		this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl");
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
		this.expiresAt = expiresAt;
	}

	/** Creates a link that never expires. */
	public static ShortLink create(String code, String targetUrl, Instant createdAt) {
		return create(code, targetUrl, createdAt, null);
	}

	public static ShortLink create(String code, String targetUrl, Instant createdAt, @Nullable Instant expiresAt) {
		return new ShortLink(code, targetUrl, createdAt, expiresAt);
	}

	public Long getId() {
		return id;
	}

	/** Called by repositories that generate identifiers themselves. */
	void assignId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public @Nullable Instant getExpiresAt() {
		return expiresAt;
	}

	/** A link is expired from its expiration instant onwards. */
	public boolean isExpiredAt(Instant now) {
		return expiresAt != null && !now.isBefore(expiresAt);
	}

}
