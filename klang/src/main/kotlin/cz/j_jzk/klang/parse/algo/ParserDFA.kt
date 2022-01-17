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

// ???
data class State(val id: Int)

// TODO better represenatation?
// TODO share some of the logic between klang and klang-re?
sealed class Action(val id: Int, val nextState: State) {
	class Shift(id: Int, nextState: State): Action(id, nextState)

	class Reduce(
		id: Int,
		val nNodes: Int,
		val reduction: (List<ASTNode>) -> ASTNode,
		nextState: State
	): Action(id, nextState)
}

class ParserDFA<T, N>(
	private val actionTable: Map<Pair<State, ASTNode>, Action>, // TODO better representation?
	private val input:  Iterator<T>, // integrate with Token or not?
	private val finalNodeType: N,
	private var state: State, // initialized with the default state
) {
	private val stack = mutableListOf<ASTNode>() // TODO better data type?

	fun <T> doParsing(): ASTNode {
		while (!isParsingFinished()) {
			val action = actionTable[state to lookahead()] ?: throw Exception("Syntax error") // TODO error handling
			when (action) {
				is Action.Shift -> state = action.nextState
				is Action.Reduce -> action.reduction(stack.popTop(action.nNodes))
			}

			state = action.nextState
		}

		return stack.first()
	}

	private fun isParsingFinished() =
		stack.firstOrNull()?.let {
			it is ASTNode.Nonterminal<*> && it.id == finalNodeType
		} ?: false

	// Lookahead and input buffering stuff
	// TODO: structure better
	// TODO: handle EOF, input with no elements etc. properly
	private fun lookahead(): ASTNode = nextInputNode
	private var nextInputNode: ASTNode = ASTNode.Terminal(input.next())
	private fun shift() {
		stack += nextInputNode
		nextInputNode = ASTNode.Terminal(input.next())
	}
}