package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.testutil.leftRecursiveDFA
import cz.j_jzk.klang.parse.testutil.rightRecursiveDFA
import cz.j_jzk.klang.parse.testutil.errorHandlingLeftRecursiveDFA
import cz.j_jzk.klang.parse.testutil.errorHandlingRightRecursiveDFA
import org.junit.Test
import kotlin.test.assertEquals

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

		assertEquals(expected, DFAParser<ASTData>(input, leftRecursiveDFA).parse())
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

		assertEquals(expected, DFAParser<ASTData>(input, rightRecursiveDFA).parse())
	}

	@Test fun testLeftRecursiveError() {
		val input = strInput("ee+e")
		val expected: ASTNode<ASTData> = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
					ASTNode.Data(
						id("e2"),
						ASTData.Nonterminal(listOf(
							ASTNode.Errorneous(id("e2")),
							node("+"),
							node("e"),
						))
					)
				))
		)

		assertEquals(expected, DFAParser<ASTData>(input, errorHandlingLeftRecursiveDFA).parse())
	}

	@Test fun testRightRecursiveError() {
		val input = strInput("e+ee")
		val expected: ASTNode<ASTData> = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
					ASTNode.Data(
						id("e2"),
						ASTData.Nonterminal(listOf(
							node("e"),
							node("+"),
							ASTNode.Errorneous(id("e2")),
						))
					)
				))
		)

		assertEquals(expected, DFAParser<ASTData>(input, errorHandlingRightRecursiveDFA).parse())
	}

	private fun node(id: String, value: String = ""): ASTNode<ASTData> = ASTNode.Data(id(id), ASTData.Terminal(value))
	private fun id(id: String) = NodeID.ID(id)
	private val eof: ASTNode<ASTData> = ASTNode.Data(NodeID.Eof, ASTData.Terminal(""))
	private fun strInput(str: String) = (str.map { node(it.toString()) } + eof).iterator()
}
