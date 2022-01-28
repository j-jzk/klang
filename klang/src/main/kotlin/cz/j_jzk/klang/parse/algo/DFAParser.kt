package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop
import cz.j_jzk.klang.util.PeekingPushbackIterator

/** This class represents the DFA (the "structure" of the parser). */
data class DFA(
	val actionTable: Map<Pair<State, NodeID>, Action>,
	val finalNodeType: NodeID,
	val startState: State
)

/**
 * This class contains all the logic of the parser.
 *
 * Because the parser needs some mutable state, this is kept separate from
 * the DFA in order to allow using the same DFA on different inputs.
 */
class DFAParser(input: Iterator<ASTNode>, val dfa: DFA) {
	private val stack = mutableListOf<ASTNode>() // TODO better data type?
	private var state = dfa.startState

	/** This method runs the parser and returns the resulting syntax tree. */
	fun parse(): ASTNode {
		while (!isParsingFinished()) {
			println("State $state, lookahead ${input.peek().id}")
			val action = dfa.actionTable[state to input.peek().id] ?: throw Exception("Syntax error") // TODO error handling
			when (action) {
				is Action.Shift -> stack += input.next()
				is Action.Reduce -> input.pushback(action.reduction(stack.popTop(action.nNodes)))
			}

			state = action.nextState
		}

		return input.next()
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
