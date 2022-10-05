package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.parse.testutil.createDFA
import cz.j_jzk.klang.parse.testutil.leftRecursiveGrammar
import cz.j_jzk.klang.parse.testutil.rightRecursiveGrammar
import cz.j_jzk.klang.parse.testutil.top
import cz.j_jzk.klang.parse.testutil.e2
import cz.j_jzk.klang.parse.testutil.fakePPPIter
import cz.j_jzk.klang.util.PositionInfo
import org.junit.Test
import kotlin.test.assertEquals

class DFAParserTest {
	@Test fun testLeftRecursion() {
		val input = fakePPPIter(listOf(node("e", "a"), node("+"), node("e", "b"), eof))
		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						ASTNode.Data(
							id("e2"),
							ASTData.Nonterminal(listOf(node("e", "a"))),
							noPos
						),
						node("+"),
						node("e", "b"),
					)),
					noPos
				)
			)),
			noPos
		)

		assertEquals(expected, DFAParser(input, createDFA(leftRecursiveGrammar)).parse())
	}

	@Test fun testRightRecursion() {
		val input = fakePPPIter(listOf(node("e", "a"), node("+"), node("e", "b"), eof))
		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						node("e", "a"),
						node("+"),
						ASTNode.Data(
							id("e2"),
							ASTData.Nonterminal(listOf(node("e", "b"))),
							noPos
						))
					),
					noPos
				)
			)),
			noPos
		)

		assertEquals(expected, DFAParser(input, createDFA(rightRecursiveGrammar)).parse())
	}

	@Test fun testLeftRecursiveError() {
		val input = fakePPPIter(strInput("ee+e"))
		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						ASTNode.Erroneous(id("e2"), noPos),
						node("+"),
						node("e"),
					)),
					noPos
				)
			)),
			noPos
		)

		assertEquals(expected, DFAParser(input, createDFA(leftRecursiveGrammar, listOf(e2, top))).parse())
	}

	@Test fun testRightRecursiveError() {
		val input = fakePPPIter(strInput("e+ee"))
		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						node("e"),
						node("+"),
						ASTNode.Erroneous(id("e2"), noPos),
					)),
					noPos
				)
			)),
			noPos
		)

		assertEquals(expected, DFAParser(input, createDFA(rightRecursiveGrammar, listOf(e2, top))).parse())
	}

	@Test fun testLeftRecursivePositionInfo() {
		val input = fakePPPIter(listOf(
			node("e", posInfo=pos(0)),
			node("+", posInfo=pos(1)),
			node("e", posInfo=pos(2)),
			ASTNode.NoValue(NodeID.Eof, pos(3)),
		))

		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						ASTNode.Data(
							id("e2"),
							ASTData.Nonterminal(listOf(node("e", posInfo=PositionInfo("in", 0)))),
							pos(0)
						),
						node("+", posInfo=pos(1)),
						node("e", posInfo=pos(2)),
					)),
					pos(0)
				)
			)),
			pos(0)
		)

		assertEquals(expected, DFAParser(input, createDFA(leftRecursiveGrammar)).parse())
	}

	@Test fun testRightRecursivePositionInfo() {
		val input = fakePPPIter(listOf(
			node("e", posInfo=pos(0)),
			node("+", posInfo=pos(1)),
			node("e", posInfo=pos(2)),
			ASTNode.NoValue(NodeID.Eof, pos(3)),
		))

		val expected: ASTNode = ASTNode.Data(
			id("top"),
			ASTData.Nonterminal(listOf(
				ASTNode.Data(
					id("e2"),
					ASTData.Nonterminal(listOf(
						node("e", posInfo=pos(0)),
						node("+", posInfo=pos(1)),
						ASTNode.Data(
							id("e2"),
							ASTData.Nonterminal(listOf(node("e", posInfo=pos(2)))),
							pos(2)
						))
					),
					pos(0)
				)
			)),
			pos(0)
		)

		assertEquals(expected, DFAParser(input, createDFA(rightRecursiveGrammar)).parse())
	}

	@Test fun testErrorCallback() {
		val input = fakePPPIter(listOf(
			node("e", posInfo=pos(0)),
			node("+", posInfo=pos(1)),
			node("e", posInfo=pos(2)),
			node("e", posInfo=pos(3)),
			ASTNode.NoValue(NodeID.Eof, pos(4)),
		))

		var timesCalled = 0
		val unexpectedNode = node("e", posInfo=pos(3))
		val expectedNodeIDs = setOf("+", NodeID.Eof)
		val errorCallback = { error: UnexpectedTokenError ->
			timesCalled++
			assertEquals(unexpectedNode, error.got)
			assertEquals(expectedNodeIDs, error.expectedIDs.toSet())
		}

		createDFA(leftRecursiveGrammar, listOf(e2, top), errorCallback).parse(input)

		assertEquals(1, timesCalled)
	}

	private fun node(id: String, value: String = "", posInfo: PositionInfo = noPos): ASTNode =
		ASTNode.Data(id(id), ASTData.Terminal(value), posInfo)
	private fun id(id: String) = NodeID.ID(id)
	private val noPos = PositionInfo("", 0)
	private val eof: ASTNode = ASTNode.Data(NodeID.Eof, ASTData.Terminal(""), noPos)
	private fun strInput(str: String) = (str.map { node(it.toString()) } + eof)
	private fun pos(n: Int) = PositionInfo("in", n)
}
