package cz.j_jzk.klang.sele

import cz.j_jzk.klang.parse.testutil.s
import cz.j_jzk.klang.lex.re.compileRegex
import kotlin.test.Test
import kotlin.test.assertEquals

class SeleBuilderTest {
	@Test fun testBasicBuild() {
		val sele = sele {
			"int" to def(re("\\d+")) { (it[0]!! as String).toInt() }
			"sum" to def("int", re("\\+"), "int") { it[0]!! as Int + it[2]!! as Int }

			ignoreRegexes("\\s+")

			errorRecovering("sum")
			setTopNode("sum")
		}.getSele()

		// TODO: compare the output with an expected value when all the basic features are complete
		println(sele)
		// assertTrue(false)
	}

	@Test fun testBasicIgnores() {
		// test that ignores added after a definition have effect
		val sele = sele {
			ignoreRegexes("before")
			"a" to def("a") { "a" }
			ignoreRegexes("after")

			setTopNode("a")
		}.getSele()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("before"), compileRegex("after")),
			s(1) to setOf(compileRegex("before"), compileRegex("after")),
		)

		assertEquals(expected, sele.parser.lexerIgnores)
	}

	@Test fun testImportableIgnores() {
		val sub = sele {
			"sub" to def(re("a")) { 0 }
			ignoreRegexes("ignA")
			setTopNode("sub")
		}
		val sup = sele {
			val a = include(sub)
			"sup" to def(re("b"), a) { 0 }
			ignoreRegexes("ignB")
			setTopNode("sup")
		}.getSele()

		val expected = mapOf(
			s(0, true) to setOf(compileRegex("ignB")),
			s(1) to setOf(compileRegex("ignA"), compileRegex("ignB")),
			s(2) to setOf(compileRegex("ignB")),
			s(3) to setOf(compileRegex("ignA")),
		)

		assertEquals(expected, sup.parser.lexerIgnores)
	}
}
