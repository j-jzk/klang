package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.Token

class TokenConverter<I, D>(private val conversions: Map<I, (String) -> D>, private val input: Iterator<Token<I>>): Iterator<ASTNode<D>> {
	/* This is for tracking if we returned an EOF already - we want to return
	 * it only once */
	private var gotEOF = false

	override fun hasNext() = input.hasNext() || !gotEOF
	override fun next(): ASTNode<D> {
		if (!input.hasNext() && !gotEOF)
			return ASTNode.NoValue(NodeID.Eof)

		val token = input.next()
		val id = NodeID.ID(token.id)
		val conversion = conversions[token.id] ?: return ASTNode.NoValue(id)
		return ASTNode.Data(id, conversion(token.value))
	}
}
