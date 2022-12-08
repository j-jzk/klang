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
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.api.AnyNodeID
import cz.j_jzk.klang.parse.UnexpectedCharacter
import java.io.EOFException
import kotlin.test.assertFailsWith

/*
 * Correct matching doesn't need to be tested extensively because it is already
 * covered by the tests of klang-re. Things to be tested are token precendence,
 * extracting the correct part of the input and others.
 */

class LexerTest {
	private val lexer = Lexer(linkedMapOf(
		re("if") to AnyNodeID("if"),
		re("\\d+") to AnyNodeID("int")
	))

	val lexerIgnoringSpaces = Lexer(
			linkedMapOf(
				re("if") to AnyNodeID("IF"),
				re("\\d+") to AnyNodeID("INT"),
			),
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
			linkedMapOf(re("\\d+") to AnyNodeID("INT")),
		)

		val input = iter("   a")
		assertEquals(null, lexer.nextToken(input, lexer.registeredTokenTypes, listOf(compileRegex(" ").fa)))
	}

	@Test fun testMaximalMunch() {
		val lexer = Lexer(linkedMapOf(
			re("a") to AnyNodeID("singleA"),
			re("aa") to AnyNodeID("doubleA"),
		))
		val input = iter("aa")
		assertEquals<Any?>(FToken("doubleA", "aa"), lexer.nextToken(input))
	}

	@Test fun testPrecedence() {
		// case 1)
		var input = iter("if")
		var ambiguousLexer = Lexer(linkedMapOf(
			re("if") to AnyNodeID("if"),
			re(".+") to AnyNodeID("anything"),
		))
		assertEquals<Any?>(FToken("if", "if"), ambiguousLexer.nextToken(input))

		// case 2)
		input = iter("if")
		ambiguousLexer = Lexer(linkedMapOf(
			re(".+") to AnyNodeID("anything"),
			re("if") to AnyNodeID("if")
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
			),
			listOf("\\s"),
		)
	}

	@Test fun testPositions() {
		testLexWithPositions(
			lexerIgnoringSpaces,
			InputFactory.fromString(" if00  if0", "a"),
			listOf(
				Token(AnyNodeID("IF"), "if", PositionInfo("a", 1)),
				Token(AnyNodeID("INT"), "00", PositionInfo("a", 3)),
				Token(AnyNodeID("IF"), "if", PositionInfo("a", 7)),
				Token(AnyNodeID("INT"), "0", PositionInfo("a", 9))
			),
			listOf("\\s"),
		)
	}

	@Test fun testUnexpectedCharacterPrecedence() {
		// test that the UnexpectedCharacter is really emitted as a last resort
		testLex(
			Lexer(linkedMapOf(
				re("[a-z]") to AnyNodeID("letter"),
			)),
			"a1-",
			listOf(
				FToken("letter", "a"),
				// <ignored character>,
				FToken(UnexpectedCharacter, "-"),
			),
			listOf("[0-9]"),
		)
	}
}
