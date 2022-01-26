package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode

// TODO move
// TODO better implementation
fun <T> MutableList<T>.popTop(nElements: Int): List<T> {
	val result = mutableListOf<T>()
	for (i in 0 until nElements)
		result += this.removeLast()
	return result
}

// TODO: typealias this to int to save memory?
data class State(val id: Int)

// TODO better represenatation?
// TODO share some of the logic between klang and klang-re?
sealed interface Action {
	val nextState: State

	data class Shift(override val nextState: State): Action

	data class Reduce(
		val nNodes: Int,
		val reduction: (List<ASTNode>) -> ASTNode,
		override val nextState: State
	): Action
}

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
