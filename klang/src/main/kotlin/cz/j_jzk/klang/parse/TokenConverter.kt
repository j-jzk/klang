package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.lex.Token
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.input.IdentifiableInput

/** An iterator that transforms Tokens from an input using specified conversion functions */
class TokenConverter<I, D>(
	private val conversions: Map<I, (String) -> D>,
	private val input: Iterator<Token<I>>,
	private val identifiableInput: IdentifiableInput
): Iterator<ASTNode<D>> {
	constructor(conversions: Map<I, (String) -> D>, input: LexerWrapper<I>.LexerIterator):
		this(conversions, input, input.input)

	/* This is for tracking if we returned an EOF already - we want to return
	 * it only once */
	private var gotEOF = false

	override fun hasNext() = input.hasNext() || !gotEOF
	override fun next(): ASTNode<D> {
		if (!input.hasNext() && !gotEOF)
			return ASTNode.NoValue(NodeID.Eof, PositionInfo(identifiableInput.id, identifiableInput.input.previousIndex()))
		if (!hasNext())
			throw NoSuchElementException()

		val token = input.next()
		val id = NodeID.ID(token.id)
		val conversion = conversions[token.id] ?: return ASTNode.NoValue(id, token.position)
		return ASTNode.Data(id, conversion(token.value), token.position)
	}
}
