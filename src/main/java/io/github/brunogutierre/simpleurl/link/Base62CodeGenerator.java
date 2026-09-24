package io.github.brunogutierre.simpleurl.link;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Component;

/**
 * Generates random 7-character Base62 codes (62^7 ≈ 3.5 trillion combinations).
 * Random codes are not guessable or enumerable, unlike sequential ones.
 */
@Component
class Base62CodeGenerator implements CodeGenerator {

	static final int CODE_LENGTH = 7;

	static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private final RandomGenerator random;

	Base62CodeGenerator() {
		this(new SecureRandom());
	}

	Base62CodeGenerator(RandomGenerator random) {
		this.random = random;
	}

	@Override
	public String generate() {
		var code = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return code.toString();
	}

}
