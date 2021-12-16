package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.testutils.iter
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerIntegrationTest {
	@Test fun testBasicLexer() {
		val lexer = lexer<String> {
			"NUMBER" to """\d+"""
			"NUMBER" to """\d+\.\d+"""
			"IF" to "if"
			"DOT" to "\\."
		}.getLexer()

		testLex(
			lexer,
			"if12.34if15if",
			listOf(
				Token("IF", "if"),
				Token("NUMBER", "12.34"),
				Token("IF", "if"),
				Token("NUMBER", "15"),
				Token("IF", "if")
			)
		)
	}

	private fun testLex(lexer: Lexer<String>, input: String, expectedTokens: List<Token<String>>) {
		val inputIter = iter(input)
		for (token in expectedTokens) {
			assertEquals(token, lexer.nextToken(inputIter))
		}
	}
}
