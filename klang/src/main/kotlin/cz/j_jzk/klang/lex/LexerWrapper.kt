package cz.j_jzk.klang.lex

import cz.j_jzk.klang.input.IdentifiableInput

/**
 * A utility class for using the lexer more easily and safely.
 * It also calls functions on certain events (currently only no match).
 */
class LexerWrapper<T>(val lexer: Lexer<T>, private val onNoMatch: (Char) -> Unit) {
	/**
	 * Matches one token and handles events (onNoMatch).
	 * When there is no match, it automatically returns the next one, but can
	 * still return null when there are no matches up to the end of input.
	 */
	private tailrec fun nextMatch(input: IdentifiableInput): Token<T>? {
		if (!input.input.hasNext()) return null

		val match = lexer.nextToken(input)

		if (match == null && input.input.hasNext()) {
			onNoMatch(input.input.next())
			return nextMatch(input)
		}

		return match
	}

	/**
	 * Returns an iterator over the tokens of an input.
	 * For example:
	 *     for (token in lexer.iterator(input)
	 *         println(token)
	 */
	fun iterator(input: IdentifiableInput) = LexerIterator(input)

	inner class LexerIterator(private val input: IdentifiableInput): Iterator<Token<T>> {
		/* We actually have to load tokens ahead of time to know if there are any
		 * or if it's just invalid characters and EOF. */
		private var nextValue: Token<T>? = null
		init {
			loadNextValue()
		}

		private fun loadNextValue() {
			nextValue =
				if (input.input.hasNext())
					nextMatch(input)
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
}
