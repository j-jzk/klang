package cz.j_jzk.klang.lex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.testutils.iter
import cz.j_jzk.klang.testutils.FToken
import cz.j_jzk.klang.parse.UnexpectedCharacter

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
		)
	}

	@Test fun testOnNoMatch() {
		testIterator(
			" 1  23x 4 x",
			listOf(
				FToken("INT", "1"),
				FToken("INT", "23"),
				FToken(UnexpectedCharacter, "x"),
				FToken("INT", "4"),
				FToken(UnexpectedCharacter, "x"),
			),
		)
	}

	@Test fun testNoMatch() {
		testIterator(
			"abc",
			listOf(
				FToken(UnexpectedCharacter, "a"),
				FToken(UnexpectedCharacter, "b"),
				FToken(UnexpectedCharacter, "c"),
			),
		)
	}

	private fun testIterator(input: String, expectedTokens: List<FToken?>) {
		val inputIterator = iter(input)
		val expectedTokensIterator = expectedTokens.iterator()
		val lexerWrapper = LexerWrapper(lexer.lexer)

		// check that all the tokens match
		while (inputIterator.input.hasNext()) {
			assertEquals<Any?>(
				expectedTokensIterator.next(),
				lexerWrapper.nextMatch(inputIterator, lexerWrapper.lexer.registeredTokenTypes, listOf(compileRegex("\\s").fa))
			)
		}

		// check that there aren't any tokens left to check
		assertFalse(expectedTokensIterator.hasNext())

		// check that there aren't any characters left in the input
		assertFalse(inputIterator.input.hasNext())
	}
}
