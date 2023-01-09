package cz.j_jzk.klang.lesana

import cz.j_jzk.klang.parse.testutil.s
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.lex.re.compileRegex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LesanaBuilderTest {
	private val int = NodeID<Int>()
	private val sum = NodeID<Int>()

	@Test fun testBasicBuild() {
		val lesana = lesana<Int> {
			int to def<String, Int>(re("\\d+")) { (it) -> it.toInt() }
			sum to def(int, re("\\+"), int) { (a, _, b) -> a + b }

			ignoreRegexes("\\s+")

			errorRecovering(sum)
			setTopNode(sum)
		}.getLesana()

		// TODO: compare the output with an expected value when all the basic features are complete
		println(lesana)
		// assertTrue(false)
	}

	@Test fun testBasicIgnores() {
		// test that ignores added after a definition have effect
		val lesana = lesana<Int> {
			ignoreRegexes("before")
			int to def(int) { (int) -> int }
			ignoreRegexes("after")

			setTopNode(int)
		}.getLesana()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("before"), compileRegex("after")),
			s(1) to setOf(compileRegex("before"), compileRegex("after")),
			s(2) to emptySet(),
		)

		assertEquals(expected, lesana.parser.lexerIgnores)
	}

	@Test fun testImportableIgnores() {
		val sub = lesana<Int> {
			int to def(re("a")) { 0 }
			ignoreRegexes("ignA")
			setTopNode(int)
		}
		val sup = lesana<Int> {
			val a = include(sub)
			sum to def(re("b"), a) { 0 }
			ignoreRegexes("ignB")
			setTopNode(sum)
		}.getLesana()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("ignB")),
			s(1) to setOf(compileRegex("ignA"), compileRegex("ignB")),
			s(2) to setOf(compileRegex("ignB")),
			s(3) to setOf(compileRegex("ignA")),
			s(4) to emptySet(),
		)

		assertEquals(expected, sup.parser.lexerIgnores)
	}

	@Test fun testImportTypeSafety() {
		val sub = lesana<String> {
			val id = NodeID<String>()

			id to def(re("a")) { "..." }
			setTopNode(id)
		}

		lesana<Int> {
			val included = include(sub)
			assertIs<NodeID<String>>(included)
		}
	}

	@Test fun testInheritIgnoredREs() {
		val subId = NodeID<String>()
		val sub = lesana<String> {
			ignoreRegexes("sub before")
			subId to def(re(".")) { "." }
			ignoreRegexes("sub between")
			inheritIgnoredREs()
			ignoreRegexes("sub after")

			setTopNode(subId)
		}

		val sup = lesana<String> {
			ignoreRegexes("sup before")
			val top = include(sub)
			ignoreRegexes("sup after")

			setTopNode(top)
		}.getLesana()

		val allIgnores = listOf("sub before", "sub between", "sub after", "sup before", "sup after")
			.map(::compileRegex).toSet()
		val expected = mapOf(
			s(0, true) to allIgnores,
			s(1) to allIgnores,
			s(2) to emptySet(),
		)

		assertEquals(expected, sup.parser.lexerIgnores)
	}
}
