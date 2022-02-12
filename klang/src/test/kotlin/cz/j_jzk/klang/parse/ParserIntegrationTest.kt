package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.api.parser
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ParserIntegrationTest {
	private val additionParser = parser<String, Int> {
		"top" to def("expr2") { it[0]!! }
		"expr2" to def("expr2", "plus", "expr") { it[0]!! + it[2]!! }
		"expr2" to def("expr") { it[0]!! }

		topNode = "top"
		errorRecovering("top", "expr2")
		conversions { }
	}.getParser()

	@Test fun testBasicParser() {
		val input = createInput("5 + 10 + 1")

		val result = additionParser.dfa.parse(input)
		assertTrue(result is ASTNode.Data)
		assertEquals(16, result.data)
	}

	@Test fun testParserWithoutTopNode() {
		assertFailsWith(IllegalArgumentException::class) {
			parser<String, Int> {
				"foo" to def("bar") { it[0]!! }
				conversions { }
			}.getParser()
		}
	}

	@Test fun testParserWithoutConversions() {
		assertFailsWith(IllegalArgumentException::class) {
			parser<String, Int> {
				"foo" to def("bar") { it[0]!! }
				topNode = "foo"
			}.getParser()
		}
	}

	@Test fun testParserWithErroneousInput() {
		val input = createInput("5 + 1 2 + 3")
		val result = additionParser.dfa.parse(input)
		assertTrue(result is ASTNode.Erroneous)
	}

	private fun createInput(input: String) =
		(input.split(" ").map { tok ->
			if (tok == "+")
				ASTNode.Data(NodeID.ID("plus"), 0)
			else
				ASTNode.Data(NodeID.ID("expr"), tok.toInt())
		} + ASTNode.Data(NodeID.Eof, 0)).iterator() // TODO: how should we handle EOF nodes IRL?
}
