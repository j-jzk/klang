package cz.j_jzk.klang.lex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.Ignore
import cz.j_jzk.klang.testutils.re
import cz.j_jzk.klang.testutils.iter
import cz.j_jzk.klang.testutils.testLex
import java.io.EOFException
import kotlin.test.assertFailsWith

/*
 * Correct matching doesn't need to be tested extensively because it is already
 * covered by the tests of klang-re. Things to be tested are token precendence,
 * extracting the correct part of the input and others.
 */

class LexerTest {
	private val lexer = Lexer<String>(linkedMapOf(
		re("if") to "if",
		re("\\d+") to "int"
	))

	@Test fun testBasicLex() {
		val input = iter("if123")

		assertEquals(Token("if", "if"), lexer.nextToken(input))
		assertEquals(Token("int", "123"), lexer.nextToken(input))
	}

	@Test fun testNoMatch() {
		val input = iter("xyz")
		assertEquals(null, lexer.nextToken(input))
	}

	@Test fun testEndOfInput() {
		val input = iter("if")
		lexer.nextToken(input)

		assertFailsWith(EOFException::class) { lexer.nextToken(input) }
	}

	@Test fun testNoMatchWithIgnored() {
		val lexer = Lexer<String>(
			linkedMapOf(re("\\d+") to "INT"),
			listOf(re(" "))
		)

		val input = iter("   a")
		assertEquals(null, lexer.nextToken(input))
	}

	@Test fun testMaximalMunch() {
		val lexer = Lexer(linkedMapOf(
			re("a") to "singleA",
			re("aa") to "doubleA"
		))
		val input = iter("aa")
		assertEquals(Token("doubleA", "aa"), lexer.nextToken(input))
	}

	@Test fun testPrecedence() {
		// case 1)
		var input = iter("if")
		var ambiguousLexer = Lexer<String>(linkedMapOf(
			re("if") to "if",
			re(".+") to "anything",
		))
		assertEquals(Token("if", "if"), ambiguousLexer.nextToken(input))

		// case 2)
		input = iter("if")
		ambiguousLexer = Lexer<String>(linkedMapOf(
			re(".+") to "anything",
			re("if") to "if"
		))
		assertEquals(Token("anything", "if"), ambiguousLexer.nextToken(input))
	}

	@Test fun testIgnore() {
		val lexer = Lexer<String>(
			linkedMapOf(
				re("if") to "IF",
				re("\\d+") to "INT"
			),
			listOf(re("\\s"))
		)

		testLex(
			lexer,
			"if 00\t0   0\nif",
			listOf(
				Token("IF", "if"),
				Token("INT", "00"),
				Token("INT", "0"),
				Token("INT", "0"),
				Token("IF", "if"),
			)
		)
	}
}
