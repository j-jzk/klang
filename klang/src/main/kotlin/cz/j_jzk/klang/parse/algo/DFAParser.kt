package cz.j_jzk.klang.parse.algo

import com.google.common.collect.Table
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.popTop
import java.io.EOFException

/** This class represents the DFA (the "structure" of the parser). */
data class DFA(
	val actionTable: Table<State, NodeID, Action>,
	val finalNodeType: NodeID,
	val startState: State,
	val errorRecoveringNodes: List<NodeID>,
	val onError: (ASTNode) -> Unit,
) {
	/** Runs the parser and returns the resulting syntax tree */
	fun parse(input: LexerPPPIterator) = DFAParser(input, this).parse()
}

/**
 * This class contains all the logic of the parser.
 *
 * Because the parser needs some mutable state, this is kept separate from
 * the DFA in order to allow using the same DFA on different inputs.
 */
internal class DFAParser(val input: LexerPPPIterator, val dfa: DFA) {
	private val nodeStack = mutableListOf<ASTNode>() // TODO better data type?
	private val stateStack = mutableListOf(dfa.startState)

	/** This method runs the parser and returns the resulting syntax tree. */
	fun parse(): ASTNode {
		while (!isParsingFinished()) {
			when (val action = dfa.actionTable[stateStack.last(), input.peek(expectedIDs())?.id]) {
				is Action.Shift -> shift(action)
				is Action.Reduce -> reduce(action)
				null -> recoverFromError()
			}
		}

		return input.next(emptyList())!!
	}

	private fun shift(action: Action.Shift) {
		nodeStack += input.next(expectedIDs())!! // the value was checked to be non-null in parse()
		stateStack += action.nextState
	}

	private fun reduce(action: Action.Reduce) {
		input.pushback(action.reduction(nodeStack.popTop(action.nNodes)))
		// Return to the state we were in before we started parsing this item
		stateStack.popTop(action.nNodes)
	}

	// FIXME: THIS WHOLE FUNCTION
	// we will need to handle error recovery differently than in the classical way
	private fun recoverFromError() {
		// for convenience
		// TODO: do error recovery in a more clever way (differentiate between EOF, unexpected token etc)
		//  - when moving to the expectedNodeTypes model, we won't have any unexpected nodes.
		val eofExc = EOFException("Unexpected EOF when recovering from an error")

		dfa.onError(input.peek(expectedIDs()) ?: throw eofExc)

		// Search the stack for an error-recovering state.
		// The position of the inserted token will be derived from the `lastRemoved`
		var lastRemoved = input.peek(expectedIDs())!!
		while (!stateStack.last().errorRecovering) {
			stateStack.removeLast()
			lastRemoved = nodeStack.removeLast()
		}

		// Act as if we've just shifted one of the error-recovering nodes
		// (we need to find a node we can actually use)
		// TODO: do this in a more clever way (now we can just hope we don't have too many err-rec nodes)
		//  -- maybe pass the information from the builder (I thought it would be easier if we don't, but
		//     it just creates this mess)
		for (node in dfa.errorRecoveringNodes) {
			if (dfa.actionTable[stateStack.last(), node] is Action.Shift) {
				stateStack += (dfa.actionTable[stateStack.last(), node] as Action.Shift).nextState
				nodeStack += ASTNode.Erroneous(node, lastRemoved.position)
				break
			}
		}

		// Skip over the input until we find a node that can appear after the dummy node
		// TODO: handle EOF properly
		// commented out to make the code compile
		// input.pushback(
		// 	input.skipUntil { dfa.actionTable.contains(stateStack.last(), it.id) }
		// 	?: throw eofExc
		// )
	}

	// TODO: this should maybe be precomputed
	private fun expectedIDs(): List<Any> =
		dfa.actionTable.row(stateStack.last()).keys.mapNotNull { nodeId ->
			when (nodeId) {
				is NodeID.ID -> nodeId.id
				is NodeID.Eof -> null
			}
		}

	// Or should we have a special finishing state?
	private fun isParsingFinished() =
		input.peek(expectedIDs())?.let {
			it.id == dfa.finalNodeType
		} ?: false

	override fun toString() = dfa.toString()
}
