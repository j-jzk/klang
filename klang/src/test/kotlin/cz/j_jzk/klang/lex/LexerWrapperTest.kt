package cz.j_jzk.klang.lex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.testutils.iter

class LexerWrapperTest {
	private val lexer = lexer<String> {
		"INT" to "\\d+"
		ignore("\\s")
	}.getLexer()

	@Test fun testBasicIterator() {
		testIterator(
			" 1  23 4",
			listOf(
				Token("INT", "1"),
				Token("INT", "23"),
				Token("INT", "4"),
			),
			0
		)
	}

	@Test fun testIgnoredCharacterAtEndOfInput() {
		testIterator(
			" 1  23 4  ",
			listOf(
				Token("INT", "1"),
				Token("INT", "23"),
				Token("INT", "4"),
			),
			0
		)
	}

	@Test fun testOnNoMatch() {
		testIterator(
			" 1  23x 4 x",
			listOf(
				Token("INT", "1"),
				Token("INT", "23"),
				Token("INT", "4"),
			),
			2
		)
	}

	@Test fun testNoMatch() {
		testIterator("abc", emptyList(), 3)
	}

	private fun testIterator(input: String, expectedTokens: List<Token<String>>, expectedInvalidChars: Int) {
		var invalidChars = 0
		val inputIterator = iter(input)
		val expectedTokensIterator = expectedTokens.iterator()
		val lexerIterator = LexerWrapper(lexer) { invalidChars++ }.iterator(inputIterator)

		// check that all the tokens match
		for (token in lexerIterator) {
			assertEquals(expectedTokensIterator.next(), token)
		}

		// check that there aren't any tokens left to check
		assertFalse(expectedTokensIterator.hasNext())

		// check the number of times onNoMatch was called
		assertEquals(expectedInvalidChars, invalidChars)

		// check that there aren't any characters left in the input
		assertFalse(inputIterator.hasNext())
	}
}
