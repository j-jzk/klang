package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.lex.api.AnyNodeID
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.testutils.testLex
import cz.j_jzk.klang.testutils.FToken
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.util.PositionInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LexerIntegrationTest {
	@Test fun testBasicLexer() {
		val lexer = lexer {
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
				FToken("IF", "if"),
			),
			listOf(" "),
		)
	}

	@Test fun testOnNoMatch() {
		var onNoMatchInvocations = 0
		val expectedTokens = listOf(
			Token(AnyNodeID("INT"), "10", PositionInfo("input", 0)),
			Token(AnyNodeID("INT"), "123", PositionInfo("input", 4)),
			Token(AnyNodeID("INT"), "5", PositionInfo("input", 8)),
			null,
		).iterator()

		val expectedUnexpectedChars = listOf(
			'x' to PositionInfo("input", 7),
			':' to PositionInfo("input", 10),
			')' to PositionInfo("input", 11),
		).iterator()

		val lexer = lexer {
			"INT" to """\d+"""
			ignore(" ")

			onNoMatch { char, position ->
				assertEquals(expectedUnexpectedChars.next(), char to position)
				onNoMatchInvocations++
			}
		}.getLexer()

		val input = InputFactory.fromString("10  123x5 :)", "input")

		while (input.input.hasNext()) {
			assertEquals(
				expectedTokens.next(),
				lexer.nextMatch(input, lexer.lexer.registeredTokenTypes, listOf(compileRegex(" ").fa))
			)
		}

		assertFalse(expectedTokens.hasNext())
		assertEquals(3, onNoMatchInvocations)
	}
}
