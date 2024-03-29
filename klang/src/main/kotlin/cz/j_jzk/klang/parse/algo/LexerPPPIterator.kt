package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.Token
import cz.j_jzk.klang.input.IdentifiableInput
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.lex.re.fa.NFA

/**
 * A parametrized peeking pushback iterator used as an interface between the lexer and the parser.
 *
 * Used internally.
 */
class LexerPPPIterator(val lexer: Lexer, val input: IdentifiableInput) {
	private val pushbackBuffer = mutableListOf<ASTNode>()
	/**
	 * Whether we have already output an EOF or not - we want to output it
	 * only once (except for pushbacks), not indefinitely
	 */
	private var wasEof = false

	val allNodeIDs: Collection<NodeID<*>> = ArrayList(lexer.registeredTokenTypes)

	/** Pushes the `node` back onto the input. */
	fun pushback(node: ASTNode) { pushbackBuffer.add(node).also { wasEof = false } }
	/**
	 * Looks at the first element of the input.
	 * (If there are nodes that were pushed back, `expectedTokenTypes` are ignored.)
	 */
	fun peek(expectedTokenTypes: Collection<NodeID<*>>, ignored: Collection<NFA>) =
		next(expectedTokenTypes, ignored)?.also { pushback(it) }

	/**
	 * Returns true if the iterator will emit any more ASTNodes.
	 * This can also change from false to true when pushing back nodes.
	 */
	fun hasNext() = !wasEof
	/**
	 * Returns the next ASTNode from the input.
	 * (If there are nodes that were pushed back, `expectedTokenTypes` are ignored.)
	 * On EOF, this function emits one EOF node and then continues to emit `null`s.
	 */
	fun next(expectedTokenTypes: Collection<NodeID<*>>, ignored: Collection<NFA>): ASTNode? =
		if (wasEof)
			null
		else if (pushbackBuffer.isNotEmpty())
			pushbackBuffer.removeLast()
		else
			convertToken(lexer.nextToken(input, expectedTokenTypes, ignored))

	/**
	 * Converts a Token into a ASTNode.Data with the data being the token's string.
	 * When token = null, returns EOF
	 */
	private fun convertToken(token: Token?): ASTNode {
		// handle EOF
		if (token == null) {
			wasEof = true
			return ASTNode.NoValue(EOFNodeID, PositionInfo(input.id, input.input.previousIndex()))
		}

		return ASTNode.Data(token.id, token.value, token.position)
	}
}
