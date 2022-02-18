package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.util.PositionInfo

/** An iterator that transforms Tokens from an input using specified conversion functions */
class TokenConverter<I, D>(
	private val conversions: Map<I, (String) -> D>,
	private val input: LexerWrapper<I>.LexerIterator
): Iterator<ASTNode<D>> {
	/* This is for tracking if we returned an EOF already - we want to return
	 * it only once */
	private var gotEOF = false

	override fun hasNext() = input.hasNext() || !gotEOF
	override fun next(): ASTNode<D> {
		if (!input.hasNext() && !gotEOF)
			return ASTNode.NoValue(NodeID.Eof, PositionInfo(input.input.id, input.input.input.previousIndex()))
		if (!hasNext())
			throw NoSuchElementException()

		val token = input.next()
		val id = NodeID.ID(token.id)
		val conversion = conversions[token.id] ?: return ASTNode.NoValue(id, token.position)
		return ASTNode.Data(id, conversion(token.value), token.position)
	}
}
