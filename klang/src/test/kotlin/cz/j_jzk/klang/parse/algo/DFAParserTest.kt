package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DFAParserTest {
	@Test fun testLeftRecursion() {
		// TODO: test the order of the operands
		val input = listOf(node("e"), node("+"), node("e"), eof).iterator()
		val expected = ASTNode(
				id("top"),
				listOf(
					ASTNode(
						id("e2"),
						listOf(
							ASTNode(
								id("e2"),
								listOf(node("e"))
							),
							node("+"),
							node("e"),
						)
					)
				)
		)

		assertEquals(expected, DFAParser(input, DFABuilderTest.leftRecursiveDFA).parse())
	}

	@Test fun testRightRecursion() {
		// TODO: test the order of the operands
		val input = listOf(node("e"), node("+"), node("e"), eof).iterator()
		val expected = ASTNode(
				id("top"),
				listOf(
					ASTNode(
						id("e2"),
						listOf(
							node("e"),
							node("+"),
							ASTNode(
								id("e2"),
								listOf(node("e"))
							),
						)
					)
				)
		)

		assertEquals(expected, DFAParser(input, DFABuilderTest.rightRecursiveDFA).parse())
	}

	@Test fun testSyntaxError() {
		val input = listOf(node("e"), node("e"), eof).iterator()
		assertFailsWith(Exception::class) {
			DFAParser(input, DFABuilderTest.leftRecursiveDFA).parse()
		}
	}

	private fun node(id: String) = ASTNode(id(id), emptyList())
	private fun id(id: String) = NodeID.ID(id)
	private val eof = ASTNode(NodeID.Eof, emptyList())
}
