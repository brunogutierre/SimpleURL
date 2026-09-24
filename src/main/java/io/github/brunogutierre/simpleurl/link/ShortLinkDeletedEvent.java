package io.github.brunogutierre.simpleurl.link;

/**
 * Published after a short link is deleted, so dependent data (such as clicks) can be
 * removed without the link package depending on it.
 */
public record ShortLinkDeletedEvent(long shortLinkId, String code) {
}
