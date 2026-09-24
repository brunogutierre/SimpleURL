package io.github.brunogutierre.simpleurl.click.dto;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * Click statistics of a short link. {@code clicksPerDay} lists only days with clicks,
 * oldest first.
 */
public record LinkStatsResponse(String code, long totalClicks, @Nullable Instant lastClickAt,
		List<DailyClicks> clicksPerDay) {
}
