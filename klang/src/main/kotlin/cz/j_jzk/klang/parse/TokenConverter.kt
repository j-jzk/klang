package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.Token

class TokenConverter<I, D>(private val conversions: Map<I, (String) -> D>, private val input: Iterator<Token<I>>): Iterator<ASTNode<D>> {
	override fun hasNext() = input.hasNext()
	override fun next(): ASTNode<D> {
		val token = input.next()
		val id = NodeID.ID(token.id)
		val conversion = conversions[token.id] ?: return ASTNode.NoValue(id)
		return ASTNode.Data(id, conversion(token.value))
	}
}
