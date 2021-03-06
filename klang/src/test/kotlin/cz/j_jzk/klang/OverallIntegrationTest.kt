package cz.j_jzk.klang

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.parse.api.parser
import cz.j_jzk.klang.input.InputFactory

class OverallIntegrationTest {
	@Test fun testIntegration() {
		val lexer = lexer<String> {
			"int" to "\\d+"
			"plus" to "\\+"
			ignore("\\s")
		}.getLexer()

		val parser = parser<String, Int> {
			conversions {
				"int" to { it.toInt() }
			}

			"top" to def("addition") { it[0]!! }
			"addition" to def("addition", "plus", "int") { it[0]!! + it[2]!! }
			"addition" to def("int") { it[0]!! }

			topNode = "top"
		}.getParser()

		val input = "12+ 8+3"
		val tokenStream = lexer.iterator(InputFactory.fromString(input, "in"))
		val result = parser.parse(tokenStream)

		assertEquals(23, result)
	}
}
