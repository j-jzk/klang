package cz.j_jzk.klang.lex

class LexerWrapper<T>(val lexer: Lexer<T>, private val onNoMatch: (Char) -> Unit) {
	/**
	 * Matches one token and handles events (onNoMatch).
	 * When there is no match, it automatically returns the next one, but can
	 * still return null when there are no matches up to the end of input.
	 */
	private fun nextMatch(input: ListIterator<Char>): Token<T>? {
		val match = lexer.nextToken(input)
		// TODO untangle this mess
		if (match == null) {
			if (input.hasNext()) {
				onNoMatch(input.next())
				return if (input.hasNext())
					nextMatch(input)
				else
					null
			} else {
				return null
			}
		}

		return match
	}

	fun iterator(input: ListIterator<Char>) = LexerIterator(input)

	inner class LexerIterator(private val input: ListIterator<Char>): Iterator<Token<T>> {
		/* We actually have to load tokens ahead of time to know if there are any
		 * or if it's just invalid characters and EOF. */
		private var nextValue: Token<T>? = null
		init {
			loadNextValue()
		}

		private fun loadNextValue() {
			nextValue =
				if (input.hasNext())
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
