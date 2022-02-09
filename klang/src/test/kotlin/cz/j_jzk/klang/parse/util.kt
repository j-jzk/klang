package cz.j_jzk.klang.parse.testutil

import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.ASTData
import cz.j_jzk.klang.parse.algo.Action
import cz.j_jzk.klang.parse.algo.State
import cz.j_jzk.klang.parse.algo.DFABuilder
import cz.j_jzk.klang.parse.NodeDef
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable

// Shorthands
val e = NodeID.ID("e")
val e2 = NodeID.ID("e2")
val p = NodeID.ID("+")
val eof = NodeID.Eof
val top = NodeID.ID("top")
fun s(i: Int) = State(i, false)
private fun shift(i: Int) = Action.Shift(s(i))
private fun reduce(len: Int) = Action.Reduce(len, exprReduction)

val topReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> = { ASTNode.Data(top, ASTData.Nonterminal(it)) }
val exprReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> = { ASTNode.Data(e2, ASTData.Nonterminal(it)) }

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

val errorHandlingDFA = DFABuilder(
		mapOf(
			top to setOf(NodeDef(listOf(e2), topReduction)),
			e2 to setOf(
				NodeDef(listOf(e2, p, e), exprReduction),
				NodeDef(listOf(e), exprReduction)
			)
		),
		top,
		listOf(e2, top)
	).build()

private fun Map<Pair<State, NodeID>, Action<ASTData>>.toTable(): Table<State, NodeID, Action<ASTData>> {
	val table = HashBasedTable.create<State, NodeID, Action<ASTData>>()
	for ((k, v) in this) {
		table[k.first, k.second] = v
	}
	return table
}