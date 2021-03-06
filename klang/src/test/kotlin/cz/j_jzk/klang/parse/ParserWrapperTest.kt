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

	private fun input(inp: String) = lexer.iterator(InputFactory.fromString(inp, "in"))

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

		val input = input("12 ++ 8 + 3")

		assertFailsWith(SyntaxError::class) { parser.parse(input) }
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

		val input = input("12 ++ 8 + 3")
		assertFailsWith(SyntaxError::class) { parser.parse(input) }
	}

	/* Regression test for https://github.com/j-jzk/klang/issues/43 */
	@Test fun testEpsilonReduction() {
		val wrapper = parser<String, Int> {
			conversions {
				"int" to { it.toInt() }
			}

			topNode = "top"
			"top" to def("list") { it[0]!! }
			"list" to def("list", "int") { it[0]!! + it[1]!! }
			"list" to def() { 0 }
		}.getParser()

		// test if the parser doesn't throw an error and that the PositionInfos are correct
		val ast = wrapper.dfa.parse(TokenConverter(wrapper.tokenConversions, input("1 2")))
		assertEquals(
			ASTNode.Data(NodeID.ID("top"), 3, PositionInfo("in", 0)),
			ast
		)
	}
}
