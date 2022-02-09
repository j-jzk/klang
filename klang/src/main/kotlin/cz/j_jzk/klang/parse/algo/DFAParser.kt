package cz.j_jzk.klang.parse.algo

import com.google.common.collect.Table
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop
import cz.j_jzk.klang.util.PeekingPushbackIterator
import cz.j_jzk.klang.util.listiterator.skipUntil

/** This class represents the DFA (the "structure" of the parser). */
data class DFA<N>(
	val actionTable: Table<State, NodeID, Action<N>>,
	val finalNodeType: NodeID,
	val startState: State,
	val errorRecoveringNodes: List<NodeID>,
)

/**
 * This class contains all the logic of the parser.
 *
 * Because the parser needs some mutable state, this is kept separate from
 * the DFA in order to allow using the same DFA on different inputs.
 */
class DFAParser<N>(input: Iterator<ASTNode<N>>, val dfa: DFA<N>) {
	private val nodeStack = mutableListOf<ASTNode<N>>() // TODO better data type?
	private val stateStack = mutableListOf(dfa.startState)

	/** This method runs the parser and returns the resulting syntax tree. */
	fun parse(): ASTNode<N> {
		while (!isParsingFinished()) {
			val action = dfa.actionTable[stateStack.last(), input.peek().id]
			when (action) {
				is Action.Shift -> shift(action)
				is Action.Reduce<*> -> reduce(action as Action.Reduce<N>)
				null -> recoverFromError()
			}
		}

		return input.next()
	}

	private fun shift(action: Action.Shift) {
		nodeStack += input.next()
		stateStack += action.nextState
	}

	private fun reduce(action: Action.Reduce<N>) {
		input.pushback(action.reduction(nodeStack.popTop(action.nNodes)))
		// Return to the state we were in before we started parsing this item
		stateStack.popTop(action.nNodes)
	}

	private fun recoverFromError() {
		// TODO: call the error reporting function supplied by the user
		// Do this more declaratively?

		// Search the stack for an error-recovering state
		while (!stateStack.last().errorRecovering) {
			stateStack.removeLast()
			nodeStack.removeLast()
		}

		// Act as if we've just parsed one of the error-recovering nodes
		// We need to find a node we can actually use
		// TODO: do this in a more clever way (now we can just hope we don't have too many err-rec nodes)
		//  -- maybe pass the information from the builder (I thought it would be easier if we don't, but it just creates this mess)
		val currentChoices = dfa.actionTable.row(stateStack.last())
		for (node in dfa.errorRecoveringNodes) {
			if (currentChoices[node] is Action.Shift) {
				stateStack += (currentChoices[node] as Action.Shift).nextState
				nodeStack += ASTNode.Errorneous(node)
				break
			}
		}

		// Skip over the input until we find a node that can appear after the dummy node
		// TODO: handle EOF properly
		input.pushback(input.skipUntil { dfa.actionTable.contains(stateStack.last(), it) } ?: throw Exception("Unexpected EOF"))
	}

	// Or should we have a special finishing state?
	private fun isParsingFinished() =
		input.peekOrNull()?.let {
			it.id == dfa.finalNodeType
		} ?: false

	// TODO: handle EOF, input with no elements etc. properly
	private val input = PeekingPushbackIterator(input)

	override fun toString() = dfa.toString()
}
