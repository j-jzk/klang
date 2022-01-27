package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop

/** This class represents the DFA (the "structure" of the parser). */
data class DFA(
	val actionTable: Map<Pair<State, NodeID>, Action>,
	val finalNodeType: NodeID,
	val startState: State
)

/**
 * This class contains all of the logic of the parser.
 *
 * Because the parser needs some mutable state, this is kept separate from
 * the DFA in order to allow using the same DFA on different inputs.
 */
class DFAParser(val input: Iterator<ASTNode>, val dfa: DFA) {
	private val stack = mutableListOf<ASTNode>() // TODO better data type?
	private var state = dfa.startState

	/** This method runs the parser and returns the resulting sytax tree. */
	fun <T> parse(): ASTNode {
		while (!isParsingFinished()) {
			val action = dfa.actionTable[state to lookahead().id] ?: throw Exception("Syntax error") // TODO error handling
			when (action) {
				is Action.Shift -> shift()
				is Action.Reduce -> stack += action.reduction(stack.popTop(action.nNodes))
			}

			state = action.nextState
		}

		return stack.first()
	}

	// Or should we have a special finishing state?
	private fun isParsingFinished() =
		stack.firstOrNull()?.let {
			it.id == dfa.finalNodeType
		} ?: false

	// Lookahead and input buffering stuff
	// TODO: structure better
	// TODO: handle EOF, input with no elements etc. properly
	private fun lookahead(): ASTNode = nextInputNode
	private var nextInputNode: ASTNode = input.next()
	private fun shift() {
		stack += nextInputNode
		nextInputNode = input.next()
	}

	override fun toString() = dfa.toString()
}
