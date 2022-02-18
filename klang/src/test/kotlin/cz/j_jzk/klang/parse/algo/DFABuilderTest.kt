package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.parse.testutil.*

/* TODO: make this less hacky
 * Specifically, find a way to structurally compare DFAs (this class currently
 * relies on the iteration order of sets, which is undefined) */
class DFABuilderTest {
	private val leftRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e2, p, e), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	private val rightRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e, p, e2), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	private fun shift(i: Int, er: Boolean = false) = Action.Shift(s(i, er))
	private fun reduce(len: Int) = Action.Reduce(len, exprReduction)
	private val emptyFun: (ASTNode<ASTData>) -> Unit = { }

	@Test fun testBasicConstruction() {
		val dfa = DFABuilder(leftRecursiveGrammar, top, emptyList(), emptyFun).build()
		val expected = DFA(
			mapOf(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(4),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(1) to p) to shift(2),
				(s(2) to e) to shift(3),
				(s(3) to eof) to reduce(3),
				(s(3) to p) to reduce(3),
				(s(4) to p) to reduce(1),
				(s(4) to eof) to reduce(1),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, identityReduction),
			).toTable(),
			top,
			s(0, true),
			emptyList(),
			emptyFun
		)
		assertEquals(expected, dfa)
	}

	@Test fun testRightRecursion() {
		val dfa = DFABuilder(rightRecursiveGrammar, top, emptyList(), emptyFun).build()
		val expected = DFA(
			mapOf(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(2),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(2) to eof) to reduce(1),
				(s(2) to p) to shift(3),
				(s(3) to e) to shift(2),
				(s(3) to e2) to shift(4),
				(s(4) to eof) to reduce(3),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, identityReduction)
			).toTable(),
			top,
			s(0, true),
			emptyList(),
			emptyFun
		)
		assertEquals(expected, dfa)
	}

	@Test fun testLeftRecursionWithErrorRecovery() {
		val dfa = DFABuilder(leftRecursiveGrammar, top, listOf(e2, top), emptyFun).build()
		val expected = DFA(
			mapOf(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(4),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(1) to p) to shift(2),
				(s(2) to e) to shift(3),
				(s(3) to eof) to reduce(3),
				(s(3) to p) to reduce(3),
				(s(4) to p) to reduce(1),
				(s(4) to eof) to reduce(1),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, identityReduction),
			).toTable(),
			top,
			s(0, true),
			listOf(e2, top),
			emptyFun
		)
		assertEquals(expected, dfa)
	}

	@Test fun testRightRecursionWithErrorRecovery() {
		val dfa = DFABuilder(rightRecursiveGrammar, top, listOf(e2, top), emptyFun).build()
		val expected = DFA(
			mapOf(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(2),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(2) to eof) to reduce(1),
				(s(2) to p) to shift(3, true),
				(s(3, true) to e) to shift(2),
				(s(3, true) to e2) to shift(4),
				(s(4) to eof) to reduce(3),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, identityReduction),
			).toTable(),
			top,
			s(0, true),
			listOf(e2, top),
			emptyFun
		)
		assertEquals(expected, dfa)
	}
}
