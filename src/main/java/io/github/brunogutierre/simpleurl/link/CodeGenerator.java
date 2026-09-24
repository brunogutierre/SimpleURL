package io.github.brunogutierre.simpleurl.link;

/**
 * Produces candidate short codes. Uniqueness is checked by the caller.
 */
@FunctionalInterface
public interface CodeGenerator {

	String generate();

}
