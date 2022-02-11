package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.api.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParserIntegrationTest {
	@Test fun testBasicParser() {
		val parser = parser<String, Int> {
			"top" to def("expr2") { it[0] }
			"expr2" to def("expr2", "plus", "expr") { it[0] + it[2] }
			"expr2" to def("expr") { it[0] }

			topNode = "top"
		}.build()

		val input = listOf(
				ASTNode.Data(NodeID.ID("expr"), 5),
				ASTNode.Data(NodeID.ID("plus"), 0),
				ASTNode.Data(NodeID.ID("expr"), 10),
				ASTNode.Data(NodeID.ID("plus"), 0),
				ASTNode.Data(NodeID.ID("expr"), 1),
				ASTNode.Data(NodeID.Eof, 0) // TODO: how should we handle EOF nodes?
		).iterator()

		val result = parser.parse(input)
		assertTrue(result is ASTNode.Data)
		assertEquals(16, result.data)
	}
}