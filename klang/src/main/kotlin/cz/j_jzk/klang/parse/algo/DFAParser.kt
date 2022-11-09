package cz.j_jzk.klang.parse.algo

import com.google.common.collect.Table
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.util.popTop
import cz.j_jzk.klang.lex.re.fa.NFA
import java.io.EOFException

/** This class represents the DFA (the "structure" of the parser). */
data class DFA(
	val actionTable: Table<State, NodeID, Action>,
	val finalNodeType: NodeID,
	val startState: State,
	val errorRecoveringNodes: List<NodeID>,
	val onUnexpectedToken: (UnexpectedTokenError) -> Unit,
	val lexerIgnores: Map<State, Set<NFA>>,
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
			when (val action = dfa.actionTable[stateStack.last(), input.peek(expectedIDs(), lexerIgnores())?.id]) {
				is Action.Shift -> shift(action)
				is Action.Reduce -> reduce(action)
				null -> recoverFromError()
			}
		}

		return input.next(emptyList(), lexerIgnores())!!
	}

	private fun shift(action: Action.Shift) {
		nodeStack += input.next(expectedIDs(), lexerIgnores())!! // the value was checked to be non-null in parse()
		stateStack += action.nextState
	}

	private fun reduce(action: Action.Reduce) {
		input.pushback(action.reduction(nodeStack.popTop(action.nNodes)))
		// Return to the state we were in before we started parsing this item
		stateStack.popTop(action.nNodes)
	}

	private fun recoverFromError() {
		val gotToken = input.peek(input.allNodeIDs, lexerIgnores()) ?: throw EOFException("Unexpected EOF in ${input.input.id}")

		dfa.onUnexpectedToken(UnexpectedTokenError(
			gotToken,
			expectedIDs()
		))

		// Search the stack for an error-recovering state.
		// The position of the inserted token will be derived from the `lastRemoved`
		var lastRemoved = gotToken
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
		input.pushback(skipUntilExpected() ?: throw EOFException("Unexpected EOF in ${input.input.id}"))
	}

	/**
	 * Skips nodes from the input until we find an expected one
	 * @return the last skipped node
	 */
	private fun skipUntilExpected(): ASTNode? {
		while (input.hasNext()) {
			val node = input.next(input.allNodeIDs, lexerIgnores()) ?: return null
			if (dfa.actionTable.contains(stateStack.last(), node.id))
				return node
		}

		return null
	}

	// TODO: this should maybe be precomputed
	private fun expectedIDs(): List<NodeID> =
		dfa.actionTable.row(stateStack.last()).keys.toList()

	private fun lexerIgnores() = dfa.lexerIgnores[stateStack.last()]!!

	// Or should we have a special finishing state?
	private fun isParsingFinished() =
		input.peek(expectedIDs(), lexerIgnores())?.let {
			it.id == dfa.finalNodeType
		} ?: false

	override fun toString() = dfa.toString()
}
