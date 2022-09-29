package cz.j_jzk.klang.lex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.testutils.iter
import cz.j_jzk.klang.testutils.FToken

class LexerWrapperTest {
	private val lexer = lexer {
		"INT" to "\\d+"
		ignore("\\s")
	}.getLexer()

	@Test fun testBasicIterator() {
		testIterator(
			" 1  23 4",
			listOf(
				FToken("INT", "1"),
				FToken("INT", "23"),
				FToken("INT", "4"),
			),
			0
		)
	}

	@Test fun testIgnoredCharacterAtEndOfInput() {
		testIterator(
			" 1  23 4  ",
			listOf(
				FToken("INT", "1"),
				FToken("INT", "23"),
				FToken("INT", "4"),
				null,
			),
			0
		)
	}

	@Test fun testOnNoMatch() {
		testIterator(
			" 1  23x 4 x",
			listOf(
				FToken("INT", "1"),
				FToken("INT", "23"),
				FToken("INT", "4"),
				null,
			),
			2
		)
	}

	@Test fun testNoMatch() {
		testIterator("abc", listOf(null), 3)
	}

	private fun testIterator(input: String, expectedTokens: List<FToken?>, expectedInvalidChars: Int) {
		var invalidChars = 0
		val inputIterator = iter(input)
		val expectedTokensIterator = expectedTokens.iterator()
		val lexerWrapper = LexerWrapper(lexer.lexer) { _, _ -> invalidChars++ }

		// check that all the tokens match
		while (inputIterator.input.hasNext()) {
			assertEquals<Any?>(
				expectedTokensIterator.next(),
				lexerWrapper.nextMatch(inputIterator, lexerWrapper.lexer.registeredTokenTypes)
			)
		}

		// check that there aren't any tokens left to check
		assertFalse(expectedTokensIterator.hasNext())

		// check the number of times onNoMatch was called
		assertEquals(expectedInvalidChars, invalidChars)

		// check that there aren't any characters left in the input
		assertFalse(inputIterator.input.hasNext())
	}
}
