package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.testutils.testLex
import cz.j_jzk.klang.testutils.FToken
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.util.PositionInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
			lexer.lexer,
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

	@Test fun testOnNoMatch() {
		var onNoMatchInvocations = 0
		val lexer = lexer<String> {
			"INT" to """\d+"""
			ignore(" ")
			onNoMatch {
				onNoMatchInvocations++
			}
		}.getLexer()
		val input = InputFactory.fromString("10  123x5 :)", "input")

		val expectedTokens = listOf(
			Token("INT", "10", PositionInfo("input", 0)),
			Token("INT", "123", PositionInfo("input", 4)),
			Token("INT", "5", PositionInfo("input", 8)),
		).iterator()

		for (token in lexer.iterator(input)) {
			assertEquals(expectedTokens.next(), token)
		}

		assertFalse(expectedTokens.hasNext())
		assertEquals(3, onNoMatchInvocations)
	}
}
