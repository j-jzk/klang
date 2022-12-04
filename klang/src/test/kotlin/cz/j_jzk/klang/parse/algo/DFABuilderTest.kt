package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.parse.testutil.*
import cz.j_jzk.klang.lex.re.CompiledRegex

/* TODO: make this less hacky
 * Specifically, find a way to structurally compare DFAs (this class currently
 * relies on the iteration order of sets, which is undefined) */
class DFABuilderTest {
	private val leftRecursiveGrammar: Map<NodeID<*>, Set<NodeDef>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e2, p, e), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	private val rightRecursiveGrammar: Map<NodeID<*>, Set<NodeDef>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e, p, e2), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	@Test fun testBasicConstruction() {
		val builder = DFABuilder(leftRecursiveGrammar, top, emptyList(), emptyFun)
		val dfa = builder.build()
		val expected = DFA(
			mapOf<Pair<State, NodeID<*>>, Action>(
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
				(s(5) to eof) to Action.Reduce(1, DFABuilder.identityReduction),
			).toTable(),
			top,
			s(0, true),
			emptyList(),
			emptyFun,
			emptyIgnoreMap(5),
		)
		assertEquals(expected, dfa)
	}

	@Test fun testRightRecursion() {
		val builder = DFABuilder(rightRecursiveGrammar, top, emptyList(), emptyFun)
		val dfa = builder.build()
		val expected = DFA(
			mapOf<Pair<State, NodeID<*>>, Action>(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(2),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(2) to eof) to reduce(1),
				(s(2) to p) to shift(3),
				(s(3) to e) to shift(2),
				(s(3) to e2) to shift(4),
				(s(4) to eof) to reduce(3),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, DFABuilder.identityReduction)
			).toTable(),
			top,
			s(0, true),
			emptyList(),
			emptyFun,
			emptyIgnoreMap(5),
		)
		assertEquals(expected, dfa)
	}

	@Test fun testLeftRecursionWithErrorRecovery() {
		val builder = DFABuilder(leftRecursiveGrammar, top, listOf(e2, top), emptyFun)
		val dfa = builder.build()
		val expected = DFA(
			mapOf<Pair<State, NodeID<*>>, Action>(
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
				(s(5) to eof) to Action.Reduce(1, DFABuilder.identityReduction),
			).toTable(),
			top,
			s(0, true),
			listOf(e2, top),
			emptyFun,
			emptyIgnoreMap(5),
		)
		assertEquals(expected, dfa)
	}

	@Test fun testRightRecursionWithErrorRecovery() {
		val builder = DFABuilder(rightRecursiveGrammar, top, listOf(e2, top), emptyFun)
		val dfa = builder.build()
		val expected = DFA(
			mapOf<Pair<State, NodeID<*>>, Action>(
				(s(0, true) to e2) to shift(1),
				(s(0, true) to e) to shift(2),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(2) to eof) to reduce(1),
				(s(2) to p) to shift(3, true),
				(s(3, true) to e) to shift(2),
				(s(3, true) to e2) to shift(4),
				(s(4) to eof) to reduce(3),
				(s(0, true) to top) to shift(5),
				(s(5) to eof) to Action.Reduce(1, DFABuilder.identityReduction),
			).toTable(),
			top,
			s(0, true),
			listOf(e2, top),
			emptyFun,
			emptyIgnoreMap(5, setOf(0, 3)),
		)
		assertEquals(expected, dfa)
	}

	// Regression test for issue #45
	@Test fun testInnerRecursion() {
		val grammar: Map<NodeID<*>, Set<NodeDef>> = mapOf(
			top to setOf(NodeDef(listOf(e2), topReduction)),
			e2 to setOf(
				NodeDef(listOf(e), exprReduction),
				NodeDef(listOf(lp, top, rp), exprReduction)
			),
		)

		val builder = DFABuilder(grammar, top, emptyList(), emptyFun)
		val dfa = builder.build()

		val expected = DFA(
			mapOf<Pair<State, NodeID<*>>, Action>(
				(s(0, true) to e) to shift(2),
				(s(0, true) to e2) to shift(1),
				(s(0, true) to top) to shift(11),
				(s(0, true) to lp) to shift(3),
				(s(1) to eof) to Action.Reduce(1, topReduction),
				(s(2) to eof) to reduce(1),
				(s(3) to e) to shift(7),
				(s(3) to e2) to shift(6),
				(s(3) to top) to shift(4),
				(s(3) to lp) to shift(8),
				(s(4) to rp) to shift(5),
				(s(5) to eof) to reduce(3),
				(s(6) to rp) to Action.Reduce(1, topReduction),
				(s(7) to rp) to reduce(1),
				(s(8) to e) to shift(7),
				(s(8) to e2) to shift(6),
				(s(8) to top) to shift(9),
				(s(8) to lp) to shift(8),
				(s(9) to rp) to shift(10),
				(s(10) to rp) to reduce(3),
				(s(11) to eof) to Action.Reduce(1, DFABuilder.identityReduction),
			).toTable(),
			top,
			s(0, true),
			emptyList(),
			emptyFun,
			emptyIgnoreMap(11),
		)

		assertEquals(expected, dfa)
	}

	private fun emptyIgnoreMap(maxStateId: Int, erStates: Set<Int> = setOf(0)): Map<State, Set<CompiledRegex>> {
		val map = mutableMapOf<State, Set<CompiledRegex>>()
		for (i in 0 until maxStateId)
			map[s(i, i in erStates)] = emptySet()
		return map
	}
}
