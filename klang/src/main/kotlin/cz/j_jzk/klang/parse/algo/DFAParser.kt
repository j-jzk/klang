package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop
import cz.j_jzk.klang.util.PeekingPushbackIterator

/** This class represents the DFA (the "structure" of the parser). */
data class DFA<N>(
	val actionTable: Map<Pair<State, NodeID>, Action<N>>,
	val finalNodeType: NodeID,
	val startState: State
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
			// TODO: proper error handling
			val action = dfa.actionTable[stateStack.last() to input.peek().id]
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
		TODO()
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
