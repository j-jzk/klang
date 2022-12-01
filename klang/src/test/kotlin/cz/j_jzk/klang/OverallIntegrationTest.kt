package cz.j_jzk.klang

import kotlin.test.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.parse.api.parser
import cz.j_jzk.klang.parse.api.AnyNodeID
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.sele.sele

class OverallIntegrationTest {
	@Test @Ignore fun testIntegration() {
		val lexer = lexer {
			"int" to "\\d+"
			"plus" to "\\+"
			ignore("\\s")
		}.getLexer()

		val parser = parser {
			conversions {
				"int" to { it.toInt() }
			}

			"top" to def("addition") { it[0]!! }
			"addition" to def("addition", "plus", "int") { (it[0]!! as Int) + (it[2]!! as Int) }
			"addition" to def("int") { it[0]!! }

			topNode = AnyNodeID("top")
		}.getParser()

		val input = "12+ 8+3"
		val tokenStream = lexer.iterator(InputFactory.fromString(input, "in"))
		// val result = parser.parse(tokenStream)
		val result = 123

		assertEquals(23, result)
	}

	@Test fun testSele() {
		val sele = sele {
			val int = NodeID<Int>()
			val sum = NodeID<Int>()

			int to def(re("\\d+")) { (re: String) -> re.toInt() }
			sum to def(sum, re("\\+"), int) { (a, _, b) -> a + b }
			sum to def(int) { it.v1 }

			val top = NodeID<Int>()
			top to def(sum) { it.v1 }

			ignoreRegexes("\\s")
			setTopNode(top)
			errorRecovering(sum)
		}.getSele()

		val input = "  12+ 8+3"
		val idIn = InputFactory.fromString(input, "in")
		assertEquals(23, sele.parse(idIn))
	}
}
