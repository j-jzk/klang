package cz.j_jzk.klang.lex

class LexerWrapper<T>(val lexer: Lexer<T>, private val onNoMatch: (Char) -> Unit) {
	/**
	 * Matches one token and handles events (onNoMatch).
	 * When there is no match, it automatically returns the next one, but can
	 * still return null when there are no matches up to the end of input.
	 */
	private fun nextMatch(input: ListIterator<Char>): Token<T>? {
		val match = lexer.nextToken(input)
		if (match == null) {
			onNoMatch(input.next())
			return if (input.hasNext())
				nextMatch(input)
			else
				null
		}

		return match
	}

	fun iterator(input: ListIterator<Char>) = object : Iterator<Token<T>?> {
		override fun hasNext() = input.hasNext()
		override fun next() = nextMatch(input)
	}
}
