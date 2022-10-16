package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.api.parser
import cz.j_jzk.klang.parse.testutil.fakePPPIter
import cz.j_jzk.klang.util.PositionInfo
import java.lang.IllegalArgumentException
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ParserIntegrationTest {
	private val additionParser = parser {
		"top" to def("expr2") { it[0]!! }
		"expr2" to def("expr2", "plus", "expr") { (it[0]!! as Int) + (it[2]!! as Int) }
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
			parser {
				"foo" to def("bar") { it[0]!! }
				conversions { }
			}.getParser()
		}
	}

	@Test @Ignore fun testParserWithoutConversions() {
		assertFailsWith(IllegalArgumentException::class) {
			parser {
				"foo" to def("bar") { it[0]!! }
				topNode = "foo"
			}.getParser()
		}
	}

	// throws OutOfMemoryError - don't know why, but we are going to refactor error handling anyway
	@Test @Ignore fun testParserWithErroneousInput() {
		val input = createInput("5 + 1 2 + 3")
		val result = additionParser.dfa.parse(input)
		assertTrue(result is ASTNode.Erroneous)
	}

	private fun createInput(input: String) =
		fakePPPIter(input.split(" ").map { tok ->
			if (tok == "+")
				ASTNode.Data("plus", 0, PositionInfo("", 0))
			else
				ASTNode.Data("expr", tok.toInt(), PositionInfo("", 0))
		} + ASTNode.Data(EOFNodeID, 0, PositionInfo("", 0)))
}
