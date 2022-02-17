package cz.j_jzk.klang.parse.testutil

import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
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
fun s(i: Int, er: Boolean = false) = State(i, er)

val topReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> =
	{ ASTNode.Data(top, ASTData.Nonterminal(it), it[0].position) }
val exprReduction: (List<ASTNode<ASTData>>) -> ASTNode<ASTData> =
	{ ASTNode.Data(e2, ASTData.Nonterminal(it), it[0].position) }

val leftRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e2, p, e), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

val rightRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e, p, e2), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

fun createDFA(
	grammar: Map<NodeID, Set<NodeDef<ASTData>>>,
	recoveryNodes: List<NodeID> = emptyList(),
	recoveryFun: (ASTNode<ASTData>) -> Unit = {}
) =
	DFABuilder(grammar, top, recoveryNodes, recoveryFun).build()

fun Map<Pair<State, NodeID>, Action<ASTData>>.toTable(): Table<State, NodeID, Action<ASTData>> {
	val table = HashBasedTable.create<State, NodeID, Action<ASTData>>()
	for ((k, v) in this) {
		table[k.first, k.second] = v
	}
	return table
}
