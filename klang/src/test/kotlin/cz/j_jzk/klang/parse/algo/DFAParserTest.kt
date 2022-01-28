package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import org.junit.Test
import kotlin.test.assertEquals

class DFAParserTest {
	@Test(timeout=2000) fun testSimpleParse() {
		// TODO: test the order of the operands
		val input = listOf(node(NodeID.EXPR), node(NodeID.PLUS), node(NodeID.EXPR), node(NodeID.EOF)).iterator()
		val expected = ASTNode(
				NodeID.TOP,
				listOf(
					ASTNode(
						NodeID.EXPR2,
						listOf(
							ASTNode(
								NodeID.EXPR2,
								listOf(ASTNode(NodeID.EXPR, listOf()))
							),
							ASTNode(NodeID.PLUS, listOf()),
							ASTNode(NodeID.EXPR, listOf())
						)
					)
				)
		)

		assertEquals(expected, DFAParser(input, DFABuilderTest.leftRecursiveDFA).parse())
	}

	private fun node(id: NodeID) = ASTNode(id, emptyList())
}