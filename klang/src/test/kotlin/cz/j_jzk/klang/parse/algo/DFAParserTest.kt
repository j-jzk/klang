package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DFAParserTest {
	@Test fun testLeftRecursion() {
		val input = listOf(node("e", "a"), node("+"), node("e", "b"), eof).iterator()
		val expected: ASTNode<ASTData> = ASTNode.Data(
				id("top"),
				ASTData.Nonterminal(listOf(
					ASTNode.Data(
						id("e2"),
						ASTData.Nonterminal(listOf(
							ASTNode.Data(
								id("e2"),
								ASTData.Nonterminal(listOf(node("e", "a")))
							),
							node("+"),
							node("e", "b"),
						))
					)
				))
		)

		assertEquals(expected, DFAParser<ASTData>(input, DFABuilderTest.leftRecursiveDFA).parse())
	}

	@Test fun testRightRecursion() {
		val input = listOf(node("e", "a"), node("+"), node("e", "b"), eof).iterator()
		val expected: ASTNode<ASTData> = ASTNode.Data(
				id("top"),
				ASTData.Nonterminal(listOf(
					ASTNode.Data(
						id("e2"),
						ASTData.Nonterminal(listOf(
							node("e", "a"),
							node("+"),
							ASTNode.Data(
								id("e2"),
								ASTData.Nonterminal(listOf(node("e", "b")))
							))
						)
					)
				))
		)

		assertEquals(expected, DFAParser<ASTData>(input, DFABuilderTest.rightRecursiveDFA).parse())
	}

	@Test fun testSyntaxError() {
		val input = listOf(node("e"), node("e"), eof).iterator()
		assertFailsWith(Exception::class) {
			DFAParser<ASTData>(input, DFABuilderTest.leftRecursiveDFA).parse()
		}
	}

	private fun node(id: String, value: String = ""): ASTNode<ASTData> = ASTNode.Data(id(id), ASTData.Terminal(value))
	private fun id(id: String) = NodeID.ID(id)
	private val eof: ASTNode<ASTData> = ASTNode.Data(NodeID.Eof, ASTData.Terminal(""))
}
