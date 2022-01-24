package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.parse.ASTNode

class ParserDFABuilderTest {
	// shorthands
	private val e = NodeID.EXPR
	private val e2 = NodeID.EXPR2
	private val p = NodeID.PLUS
	private val eof = NodeID.EOF
	private fun s(i: Int) = State(i)
	private fun shift(i: Int) = Action.Shift(s(i))
	private fun reduce(len: Int, nextState: Int) = Action.Reduce(len, exprReduction, s(nextState))

	private val topReduction = { it: List<ASTNode> -> it[0] }
	private val exprReduction = { it: List<ASTNode> -> ASTNode(NodeID.EXPR2, it) }

	// This construction works, but the assertion fails for some reason
	@Test fun testBasicConstruction() {
		val dfa = ParserDFABuilder(
			mapOf(
				NodeID.TOP to setOf(NodeDef(listOf(NodeID.EXPR2), topReduction)),
				NodeID.EXPR2 to setOf(
					NodeDef(listOf(NodeID.EXPR2, NodeID.PLUS, NodeID.EXPR), exprReduction),
					NodeDef(listOf(NodeID.EXPR), exprReduction)
				)
			),
			NodeID.TOP
		).go(listOf(ASTNode(NodeID.EOF, emptyList())).iterator())

		val expected = ParserDFA(
			mapOf(
				(s(0) to e2) to shift(2),
				(s(0) to e) to shift(1),
				(s(2) to eof) to Action.Reduce(1, topReduction, s(0)),
				(s(2) to p) to shift(3),
				(s(3) to e) to shift(4),
				(s(4) to eof) to reduce(3, 0),
				(s(4) to p) to reduce(3, 0),
				(s(1) to p) to reduce(1, 0),
				(s(1) to eof) to reduce(1, 0),
			),
			listOf(ASTNode(NodeID.EOF, emptyList())).iterator(),
			NodeID.TOP,
			s(0)
		)

		assertEquals(expected, dfa)
	}

	// This construction gets caught in an infinite loop
	@Test fun testRightRecursion() {
		val dfa = ParserDFABuilder(
			mapOf(
				NodeID.TOP to setOf(NodeDef(listOf(NodeID.EXPR2), topReduction)),
				NodeID.EXPR2 to setOf(
					NodeDef(listOf(NodeID.EXPR, NodeID.PLUS, NodeID.EXPR2), exprReduction),
					NodeDef(listOf(NodeID.EXPR), exprReduction)
				)
			),
			NodeID.TOP
		).go(listOf(ASTNode(NodeID.EOF, emptyList())).iterator())

		/*val expected = ParserDFA(
			mapOf(
				(s(0) to e2) to shift(2),
				(s(0) to e) to shift(1),
				(s(2) to eof) to Action.Reduce(1, topReduction, s(0)),
				(s(2) to p) to shift(3),
				(s(3) to e) to shift(4),
				(s(4) to eof) to reduce(3, 0),
				(s(4) to p) to reduce(3, 0),
				(s(1) to p) to reduce(1, 0),
				(s(1) to eof) to reduce(1, 0),
			),
			listOf(ASTNode(NodeID.EOF, emptyList())).iterator(),
			NodeID.TOP,
			s(0)
		)

		assertEquals(expected, dfa)*/
	}
}