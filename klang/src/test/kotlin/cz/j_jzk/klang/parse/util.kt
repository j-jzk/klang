package cz.j_jzk.klang.parse.testutil

import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.testutil.PeekingPushbackIterator
import cz.j_jzk.klang.parse.algo.ASTData
import cz.j_jzk.klang.parse.algo.Action
import cz.j_jzk.klang.parse.algo.LexerPPPIterator
import cz.j_jzk.klang.parse.algo.State
import cz.j_jzk.klang.parse.algo.DFABuilder
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.parse.api.AnyNodeID
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.algo.DFA
// import org.mockito.kotlin.mock
// import org.mockito.ArgumentMatchers
import io.mockk.mockk
import io.mockk.every

// Shorthands
val e = AnyNodeID("e")
val e2 = AnyNodeID("e2")
val p = AnyNodeID("+")
val lp = AnyNodeID("(")
val rp = AnyNodeID(")")
val eof = EOFNodeID
val top = AnyNodeID("top")
fun id(v: String) = AnyNodeID(v)
fun s(i: Int, er: Boolean = false) = State(i, er)

fun shift(i: Int, er: Boolean = false) = Action.Shift(s(i, er))
fun reduce(len: Int) = Action.Reduce(len, exprReduction)
val emptyFun: (UnexpectedTokenError) -> Unit = { }

val topReduction: (List<ASTNode>) -> ASTNode =
	{ ASTNode.Data(top, ASTData.Nonterminal(it), it[0].position) }
val exprReduction: (List<ASTNode>) -> ASTNode =
	{ ASTNode.Data(e2, ASTData.Nonterminal(it), it[0].position) }
val expr1Reduction: (List<ASTNode>) -> ASTNode =
    { ASTNode.Data(e, ASTData.Nonterminal(it), it[0].position) }
val identityReduction: (List<ASTNode>) -> ASTNode =
	{ it[0] }

val leftRecursiveGrammar: Map<NodeID<*>, Set<NodeDef>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e2, p, e), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

val rightRecursiveGrammar: Map<NodeID<*>, Set<NodeDef>> = mapOf(
	top to setOf(NodeDef(listOf(e2), topReduction)),
	e2 to setOf(
		NodeDef(listOf(e, p, e2), exprReduction),
		NodeDef(listOf(e), exprReduction)
	)
)

fun createDFA(
	grammar: Map<NodeID<*>, Set<NodeDef>>,
	recoveryNodes: List<NodeID<*>> = emptyList(),
	recoveryFun: (UnexpectedTokenError) -> Unit = {}
) =
	DFABuilder(grammar, top, recoveryNodes, recoveryFun).build()

fun Map<Pair<State, NodeID<*>>, Action>.toTable(): Table<State, NodeID<*>, Action> {
	val table = HashBasedTable.create<State, NodeID<*>, Action>()
	for ((k, v) in this) {
		table[k.first, k.second] = v
	}
	return table
}

// helper functions for fakePPPIter
private fun peekId(iter: PeekingPushbackIterator<ASTNode?>): Any? = iter.peek()?.id

private fun getNextIfExpected(
	iter: PeekingPushbackIterator<ASTNode?>,
	expectedIDs: Collection<Any>,
	returnIfExpected: () -> ASTNode?
): ASTNode? =
	if (expectedIDs.contains(peekId(iter)))
		returnIfExpected()
	else if (iter.hasItemsInPushbackBuffer)
		// as with a real LexerPPPIterator, if we are getting items from the pushback buffer,
		// we don't check if they are expected or not.
		returnIfExpected()
	else
		null

fun fakePPPIter(nodes: List<ASTNode?>): LexerPPPIterator =
	mockk<LexerPPPIterator>().also { mock ->
		val ppIter = PeekingPushbackIterator(nodes.iterator())
		every { mock.next(any(), any()) } answers { getNextIfExpected(ppIter, firstArg<Collection<Any>>(), ppIter::next) }
		every { mock.peek(any(), any()) } answers { getNextIfExpected(ppIter, firstArg<Collection<Any>>(), ppIter::peek) }
		every { mock.pushback(any()) } answers { ppIter.pushback(firstArg()) }
		every { mock.hasNext() } answers { ppIter.hasNext() }
		every { mock.allNodeIDs } answers { nodes.mapNotNull { it?.id } }
	}

/**
 * Useful for manual DFA debugging
 */
fun DFA.actionTableToString(): String {
    val sb = StringBuilder()
    for ((state, actions) in actionTable.rowMap()) {
        sb.appendLine("${state.toString()} {")
        for ((lookahead, action) in actions) {
            sb.append('\t')
            sb.append(
                if (lookahead is AnyNodeID) "\"${lookahead.v}\"" else lookahead.toString()
            )
            sb.append(": ")
            when (action) {
                is Action.Shift -> sb.append("shift(nextState=${action.nextState.id})")
                is Action.Reduce -> {
                    sb.append("reduce(n=${action.nNodes}, ")
                    sb.append(when (action.reduction) {
                        topReduction -> "topReduction"
                        exprReduction -> "exprReduction"
                        expr1Reduction -> "expr1Reduction"
                        else -> "???"
                    })
                    sb.append(")")
                }
            }
            sb.appendLine()
        }
        sb.appendLine("}")
    }
    return sb.toString()
}
