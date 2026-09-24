package io.github.brunogutierre.simpleurl.click.dto;

import java.time.LocalDate;

/**
 * Number of clicks on one day (UTC).
 */
public record DailyClicks(LocalDate date, long clicks) {
}
