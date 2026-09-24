package io.github.brunogutierre.simpleurl.link;

import java.util.HashSet;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Base62CodeGeneratorTest {

	@Test
	void generatesDistinctSevenCharacterBase62Codes() {
		var generator = new Base62CodeGenerator();
		var codes = new HashSet<String>();

		Stream.generate(generator::generate).limit(10_000).forEach(codes::add);

		assertThat(codes).hasSize(10_000).allMatch(code -> code.matches("[0-9A-Za-z]{7}"));
	}

	@Test
	void usesWholeAlphabet() {
		var generator = new Base62CodeGenerator(new Random(42));
		var seen = new HashSet<Integer>();

		Stream.generate(generator::generate).limit(1_000).forEach(code -> code.chars().forEach(seen::add));

		assertThat(seen).hasSize(Base62CodeGenerator.ALPHABET.length());
	}

}
