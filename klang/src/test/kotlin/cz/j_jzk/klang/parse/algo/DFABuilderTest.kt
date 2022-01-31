package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID

/* TODO: make this less hacky
 * Specifically, find a way to structurally compare DFAs (this class currently
 * relies on the iteration order of sets, which is undefined) */
class DFABuilderTest {


	@Test fun testBasicConstruction() {
		val dfa = DFABuilder(
			mapOf(
				NodeID.TOP to setOf(NodeDef(listOf(NodeID.EXPR2), topReduction)),
				NodeID.EXPR2 to setOf(
					NodeDef(listOf(NodeID.EXPR2, NodeID.PLUS, NodeID.EXPR), exprReduction),
					NodeDef(listOf(NodeID.EXPR), exprReduction)
				)
			),
			NodeID.TOP
		).build()

		assertEquals(leftRecursiveDFA, dfa)
	}

	@Test fun testRightRecursion() {
		val dfa = DFABuilder(
			mapOf(
				NodeID.TOP to setOf(NodeDef(listOf(NodeID.EXPR2), topReduction)),
				NodeID.EXPR2 to setOf(
					NodeDef(listOf(NodeID.EXPR, NodeID.PLUS, NodeID.EXPR2), exprReduction),
					NodeDef(listOf(NodeID.EXPR), exprReduction)
				)
			),
			NodeID.TOP
		).build()

		assertEquals(rightRecursiveDFA, dfa)
	}

	companion object {
		// shorthands
		private val e = NodeID.EXPR
		private val e2 = NodeID.EXPR2
		private val p = NodeID.PLUS
		private val eof = NodeID.EOF
		private fun s(i: Int) = State(i)
		private fun shift(i: Int) = Action.Shift(s(i))
		private fun reduce(len: Int) = Action.Reduce(len, exprReduction)

		private val topReduction: (List<ASTNode>) -> ASTNode = { ASTNode(NodeID.TOP, it) }
		private val exprReduction: (List<ASTNode>) -> ASTNode = { ASTNode(NodeID.EXPR2, it) }

		val leftRecursiveDFA = DFA(
				mapOf(
						(s(0) to e2) to shift(1),
						(s(0) to e) to shift(4),
						(s(1) to eof) to Action.Reduce(1, topReduction),
						(s(1) to p) to shift(2),
						(s(2) to e) to shift(3),
						(s(3) to eof) to reduce(3),
						(s(3) to p) to reduce(3),
						(s(4) to p) to reduce(1),
						(s(4) to eof) to reduce(1),
				),
				NodeID.TOP,
				s(0)
		)

		val rightRecursiveDFA = DFA(
				mapOf(
						(s(5) to e2) to shift(6),
						(s(5) to e) to shift(7),
						(s(6) to eof) to Action.Reduce(1, topReduction),
						(s(7) to eof) to reduce(1),
						(s(7) to p) to shift(8),
						(s(8) to e) to shift(7),
						(s(8) to e2) to shift(9),
						(s(9) to eof) to reduce(3),
				),
				NodeID.TOP,
				s(5)
		)
	}
}
