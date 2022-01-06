package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.testutils.iter
import cz.j_jzk.klang.testutils.testLex
import cz.j_jzk.klang.testutils.FToken
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerIntegrationTest {
	@Test fun testBasicLexer() {
		val lexer = lexer<String> {
			"NUMBER" to """\d+"""
			"NUMBER" to """\d+\.\d+"""
			"IF" to "if"
			"DOT" to "\\."
			ignore(" ")
		}.getLexer()

		testLex(
			lexer,
			"if 12.34    if15 if ",
			listOf(
				FToken("IF", "if"),
				FToken("NUMBER", "12.34"),
				FToken("IF", "if"),
				FToken("NUMBER", "15"),
				FToken("IF", "if")
			)
		)
	}
}
