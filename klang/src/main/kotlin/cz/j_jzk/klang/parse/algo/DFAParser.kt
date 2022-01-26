package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop

data class DFA(
	val actionTable: Map<Pair<State, NodeID>, Action>,
	val finalNodeType: NodeID,
	val startState: State
)

/* Because the parser needs some mutable state, we separate it into
 * a different class to be able to use the same DFA on different inputs. */
class DFAParser(val input: Iterator<ASTNode>, val dfa: DFA) {
	private val stack = mutableListOf<ASTNode>() // TODO better data type?
	private var state = dfa.startState

	fun <T> doParsing(): ASTNode {
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
