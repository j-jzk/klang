package cz.j_jzk.klang.lex

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.testutils.re
import cz.j_jzk.klang.testutils.iter
import cz.j_jzk.klang.testutils.testLex
import cz.j_jzk.klang.testutils.FToken
import cz.j_jzk.klang.testutils.testLexWithPositions
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.util.PositionInfo
import java.io.EOFException
import kotlin.test.assertFailsWith

/*
 * Correct matching doesn't need to be tested extensively because it is already
 * covered by the tests of klang-re. Things to be tested are token precendence,
 * extracting the correct part of the input and others.
 */

class LexerTest {
	private val lexer = Lexer(linkedMapOf(
		re("if") to "if",
		re("\\d+") to "int"
	))

	val lexerIgnoringSpaces = Lexer(
			linkedMapOf(
				re("if") to "IF",
				re("\\d+") to "INT"
			),
			listOf(re("\\s"))
		)

	@Test fun testBasicLex() {
		val input = iter("if123")

		assertEquals<Any?>(FToken("if", "if"), lexer.nextToken(input))
		assertEquals<Any?>(FToken("int", "123"), lexer.nextToken(input))
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
		val lexer = Lexer(
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
		assertEquals<Any?>(FToken("doubleA", "aa"), lexer.nextToken(input))
	}

	@Test fun testPrecedence() {
		// case 1)
		var input = iter("if")
		var ambiguousLexer = Lexer(linkedMapOf(
			re("if") to "if",
			re(".+") to "anything",
		))
		assertEquals<Any?>(FToken("if", "if"), ambiguousLexer.nextToken(input))

		// case 2)
		input = iter("if")
		ambiguousLexer = Lexer(linkedMapOf(
			re(".+") to "anything",
			re("if") to "if"
		))
		assertEquals<Any?>(FToken("anything", "if"), ambiguousLexer.nextToken(input))
	}

	@Test fun testIgnore() {
		testLex(
			lexerIgnoringSpaces,
			"if 00\t0   0\nif",
			listOf(
				FToken("IF", "if"),
				FToken("INT", "00"),
				FToken("INT", "0"),
				FToken("INT", "0"),
				FToken("IF", "if"),
			)
		)
	}

	@Test fun testPositions() {
		testLexWithPositions(
			lexerIgnoringSpaces,
			InputFactory.fromString(" if00  if0", "a"),
			listOf(
				Token("IF", "if", PositionInfo("a", 1)),
				Token("INT", "00", PositionInfo("a", 3)),
				Token("IF", "if", PositionInfo("a", 7)),
				Token("INT", "0", PositionInfo("a", 9))
			)
		)
	}
}
