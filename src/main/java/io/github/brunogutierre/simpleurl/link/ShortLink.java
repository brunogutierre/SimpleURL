package io.github.brunogutierre.simpleurl.link;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A short code that points to a target URL. Maps the {@code short_link} table.
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

	/** Required by JPA. */
	protected ShortLink() {
	}

	private ShortLink(String code, String targetUrl, Instant createdAt) {
		this.code = Objects.requireNonNull(code, "code");
		this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl");
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
	}

	public static ShortLink create(String code, String targetUrl, Instant createdAt) {
		return new ShortLink(code, targetUrl, createdAt);
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

}
