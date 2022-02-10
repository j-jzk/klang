package cz.j_jzk.klang.parse.testutil

import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.ASTData
import cz.j_jzk.klang.parse.algo.Action
import cz.j_jzk.klang.parse.algo.State
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable

// Shorthands
val e = NodeID.ID("e")
val e2 = NodeID.ID("e2")
val p = NodeID.ID("+")
val eof = NodeID.Eof
val top = NodeID.ID("top")
fun s(i: Int, er: Boolean = false) = State(i, er)
private fun shift(i: Int, er: Boolean = false) = Action.Shift(s(i, er))
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
		(s(0) to e2) to shift(1),
		(s(0) to e) to shift(2),
		(s(1) to eof) to Action.Reduce(1, topReduction),
		(s(2) to eof) to reduce(1),
		(s(2) to p) to shift(3),
		(s(3) to e) to shift(2),
		(s(3) to e2) to shift(4),
		(s(4) to eof) to reduce(3),
	).toTable(),
	top,
	s(0),
	emptyList()
)

val errorHandlingLeftRecursiveDFA = DFA(
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
	).toTable(),
	top,
	s(0, true),
	listOf(e2, top)
)

val errorHandlingRightRecursiveDFA = DFA(
	mapOf(
		(s(0, true) to e2) to shift(1),
		(s(0, true) to e) to shift(2),
		(s(1) to eof) to Action.Reduce(1, topReduction),
		(s(2) to eof) to reduce(1),
		(s(2) to p) to shift(3, true),
		(s(3, true) to e) to shift(2),
		(s(3, true) to e2) to shift(4),
		(s(4) to eof) to reduce(3),
	).toTable(),
	top,
	s(0, true),
	listOf(e2, top)
)

private fun Map<Pair<State, NodeID>, Action<ASTData>>.toTable(): Table<State, NodeID, Action<ASTData>> {
	val table = HashBasedTable.create<State, NodeID, Action<ASTData>>()
	for ((k, v) in this) {
		table[k.first, k.second] = v
	}
	return table
}
