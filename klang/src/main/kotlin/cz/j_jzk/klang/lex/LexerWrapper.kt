package cz.j_jzk.klang.lex

import cz.j_jzk.klang.input.IdentifiableInput
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.lex.re.fa.NFA

/**
 * A utility class for using the lexer more easily and safely.
 * It also calls functions on certain events (currently only no match).
 */
class LexerWrapper(val lexer: Lexer) {
	/**
	 * Matches one token. Returns null on EOF.
	 */
	fun nextMatch(
		input: IdentifiableInput,
		expectedTokenTypes: Collection<NodeID<*>>,
		ignored: Collection<NFA>
	): Token? {
		if (!input.input.hasNext()) return null

		val match = lexer.nextToken(input, expectedTokenTypes, ignored)

		return match
	}

	/**
	 * Returns an iterator over the tokens of an input.
	 * For example:
	 *     for (token in lexer.iterator(input))
	 *         println(token)
	 */
	fun iterator(input: IdentifiableInput) = LexerIterator(input)

	@Suppress("UndocumentedPublicClass")
	inner class LexerIterator(val input: IdentifiableInput): Iterator<Token> {
		/* We actually have to load tokens ahead of time to know if there are any
		 * or if it's just invalid characters and EOF. */
		private var nextValue: Token? = null
		init {
			loadNextValue()
		}

		private fun loadNextValue() {
			nextValue =
				if (input.input.hasNext())
					nextMatch(input, emptyList(), emptyList()) // TODO
				else
					null
		}

		override fun hasNext() = nextValue != null
		override fun next() =
			nextValue?.let {
				loadNextValue()
				it
			} ?: throw NoSuchElementException()
	}

	override fun toString(): String = "LexerWrapper(lexer=$lexer)"
}
