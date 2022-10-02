package cz.j_jzk.klang.parse.testutil

import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.util.PeekingPushbackIterator
import cz.j_jzk.klang.parse.algo.ASTData
import cz.j_jzk.klang.parse.algo.Action
import cz.j_jzk.klang.parse.algo.LexerPPPIterator
import cz.j_jzk.klang.parse.algo.State
import cz.j_jzk.klang.parse.algo.DFABuilder
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.UnexpectedTokenError
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable
// import org.mockito.kotlin.mock
// import org.mockito.ArgumentMatchers
import io.mockk.mockk
import io.mockk.every

// Shorthands
val e = NodeID.ID("e")
val e2 = NodeID.ID("e2")
val p = NodeID.ID("+")
val lp = NodeID.ID("(")
val rp = NodeID.ID(")")
val eof = NodeID.Eof
val top = NodeID.ID("top")
fun s(i: Int, er: Boolean = false) = State(i, er)

val topReduction: (List<ASTNode>) -> ASTNode =
	{ ASTNode.Data(top, ASTData.Nonterminal(it), it[0].position) }
val exprReduction: (List<ASTNode>) -> ASTNode =
	{ ASTNode.Data(e2, ASTData.Nonterminal(it), it[0].position) }
val identityReduction: (List<ASTNode>) -> ASTNode =
	{ it[0] }

val leftRecursiveGrammar: Map<NodeID, Set<NodeDef>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e2, p, e), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

val rightRecursiveGrammar: Map<NodeID, Set<NodeDef>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e, p, e2), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

fun createDFA(
	grammar: Map<NodeID, Set<NodeDef>>,
	recoveryNodes: List<NodeID> = emptyList(),
	recoveryFun: (UnexpectedTokenError) -> Unit = {}
) =
	DFABuilder(grammar, top, recoveryNodes, recoveryFun).build()

fun Map<Pair<State, NodeID>, Action>.toTable(): Table<State, NodeID, Action> {
	val table = HashBasedTable.create<State, NodeID, Action>()
	for ((k, v) in this) {
		table[k.first, k.second] = v
	}
	return table
}

fun fakePPPIter(nodes: List<ASTNode>): LexerPPPIterator =
	mockk<LexerPPPIterator>().also { mock ->
		val ppIter = PeekingPushbackIterator(nodes.iterator())
		every { mock.next(any()) } answers { ppIter.next() }
		every { mock.peek(any()) } answers { ppIter.peek() }
		every { mock.pushback(any()) } answers { ppIter.pushback(firstArg()) }
		every { mock.hasNext() } answers { ppIter.hasNext() }
	}
