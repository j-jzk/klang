package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.set

/* TODO: make this less hacky
 * Specifically, find a way to structurally compare DFAs (this class currently
 * relies on the iteration order of sets, which is undefined) */
class DFABuilderTest {
	@Test fun testBasicConstruction() {
		val dfa = DFABuilder(
			mapOf(
				top to setOf(NodeDef(listOf(e2), topReduction)),
				e2 to setOf(
					NodeDef(listOf(e2, p, e), exprReduction),
					NodeDef(listOf(e), exprReduction)
				)
			),
			top,
			emptyList()
		).build()

		assertEquals(leftRecursiveDFA, dfa)
	}

	@Test fun testRightRecursion() {
		val dfa = DFABuilder(
			mapOf(
				top to setOf(NodeDef(listOf(e2), topReduction)),
				e2 to setOf(
					NodeDef(listOf(e, p, e2), exprReduction),
					NodeDef(listOf(e), exprReduction)
				)
			),
			top,
			emptyList()
		).build()

		assertEquals(rightRecursiveDFA, dfa)
	}

	companion object {
		// shorthands
		private val e = NodeID.ID("e")
		private val e2 = NodeID.ID("e2")
		private val p = NodeID.ID("+")
		private val eof = NodeID.Eof
		private val top = NodeID.ID("top")
		private fun s(i: Int) = State(i, false)
		private fun shift(i: Int) = Action.Shift(s(i))
		private fun reduce(len: Int) = Action.Reduce(len, exprReduction)

		private val topReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> = { ASTNode.Data(top, ASTData.Nonterminal(it)) }
		private val exprReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> = { ASTNode.Data(e2, ASTData.Nonterminal(it)) }

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
				).toTable(),
				top,
				s(0),
				emptyList()
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
				).toTable(),
				top,
				s(5),
				emptyList()
		)

		private fun Map<Pair<State, NodeID>, Action<ASTData>>.toTable(): Table<State, NodeID, Action<ASTData>> {
			val table = HashBasedTable.create<State, NodeID, Action<ASTData>>()
			for ((k, v) in this) {
				table[k.first, k.second] = v
			}
			return table
		}
	}
}
