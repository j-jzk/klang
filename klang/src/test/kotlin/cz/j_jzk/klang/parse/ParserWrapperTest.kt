package cz.j_jzk.klang.parse

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.parse.api.parser
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.util.PositionInfo

class ParserWrapperTest {
	private val lexer = lexer<String> {
		"int" to "\\d+"
		"plus" to "\\+"
		ignore("\\s")
	}.getLexer()

	@Test fun testErrorRecoveryInWrapper() {
		var numberOfErrors = 0
		val expectedErroneousNode = ASTNode.NoValue<Int>(NodeID.ID("plus"), PositionInfo("in", 4))

		val parser = parser<String, Int> {
			conversions {
				"int" to { it.toInt() }
			}

			"top" to def("addition") { it[0]!! }
			"addition" to def("addition", "plus", "int") { it[0]!! + it[2]!! }
			"addition" to def("int") { it[0]!! }

			topNode = "top"

			errorRecovering("top", "addition")

			onError { node ->
				numberOfErrors++
				assertEquals(expectedErroneousNode, node)
			}
		}.getParser()

		val input = "12 ++ 8 + 3"
		val tokenStream = lexer.iterator(InputFactory.fromString(input, "in"))

		assertFailsWith(SyntaxError::class) { parser.parse(tokenStream) }
		assertEquals(1, numberOfErrors)
	}

	/* Tests if the parser doesn't break down in flames when we don't specify
	 * any error-recovering nodes */
	@Test fun testDefaultErrorRecovery() {
		val parser = parser<String, Int> {
			conversions {
				"int" to { it.toInt() }
			}

			"top" to def("addition") { it[0]!! }
			"addition" to def("addition", "plus", "int") { it[0]!! + it[2]!! }
			"addition" to def("int") { it[0]!! }

			topNode = "top"
		}.getParser()

		val input = "12 ++ 8 + 3"
		val tokenStream = lexer.iterator(InputFactory.fromString(input, "in"))

		assertFailsWith(SyntaxError::class) { parser.parse(tokenStream) }
	}
}
