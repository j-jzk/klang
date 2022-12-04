package cz.j_jzk.klang.sele

import cz.j_jzk.klang.parse.testutil.s
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.lex.re.compileRegex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SeleBuilderTest {
	private val int = NodeID<Int>()
	private val sum = NodeID<Int>()

	@Test fun testBasicBuild() {
		val sele = sele<Int> {
			int to def<String, Int>(re("\\d+")) { (it) -> it.toInt() }
			sum to def(int, re("\\+"), int) { (a, _, b) -> a + b }

			ignoreRegexes("\\s+")

			errorRecovering(sum)
			setTopNode(sum)
		}.getSele()

		// TODO: compare the output with an expected value when all the basic features are complete
		println(sele)
		// assertTrue(false)
	}

	@Test fun testBasicIgnores() {
		// test that ignores added after a definition have effect
		val sele = sele<Int> {
			ignoreRegexes("before")
			int to def(int) { (int) -> int }
			ignoreRegexes("after")

			setTopNode(int)
		}.getSele()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("before"), compileRegex("after")),
			s(1) to setOf(compileRegex("before"), compileRegex("after")),
		)

		assertEquals(expected, sele.parser.lexerIgnores)
	}

	@Test fun testImportableIgnores() {
		val sub = sele<Int> {
			int to def(re("a")) { 0 }
			ignoreRegexes("ignA")
			setTopNode(int)
		}
		val sup = sele<Int> {
			val a = include(sub)
			sum to def(re("b"), a) { 0 }
			ignoreRegexes("ignB")
			setTopNode(sum)
		}.getSele()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("ignB")),
			s(1) to setOf(compileRegex("ignA"), compileRegex("ignB")),
			s(2) to setOf(compileRegex("ignB")),
			s(3) to setOf(compileRegex("ignA")),
		)

		assertEquals(expected, sup.parser.lexerIgnores)
	}

	@Test fun testImportTypeSafety() {
		val sub = sele<String> {
			val id = NodeID<String>()

			id to def(re("a")) { "..." }
			setTopNode(id)
		}

		sele<Int> {
			val included = include(sub)
			assertIs<NodeID<String>>(included)
		}
	}
}
