package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.MultipleMatcher
import cz.j_jzk.klang.util.listiterator.previousString

class Lexer<T>(val tokenDefs: LinkedHashMap<NFA, T>) {
	val matcher = MultipleMatcher(tokenDefs.keys)
	val precedenceTable: Map<NFA, Int>
	init {
		var i = 0
		precedenceTable = tokenDefs.map { (k, _) -> k to i++ }.toMap()
	}

	fun nextToken(input: ListIterator<Char>): Token<T>? {
		val longestMatch = chooseMatch(matcher.nextMatch(input))

		if (longestMatch == null)
			return null

		return Token(
			tokenDefs[longestMatch.key]!!,
			input.previousString(longestMatch.value)
		)
	}

	/**
	 * Chooses a token according to maximal-munch and precedence of tokens
	 */
	private fun chooseMatch(matches: Map<NFA, Int>): Map.Entry<NFA, Int>?
		= matches.maxWithOrNull { a, b ->
			when {
				// maximal munch
				a.value > b.value -> 1
				// token precedence
				a.value == b.value && doesTokenPrecede(a.key, b.key) -> 1
				a === b -> 0
				else -> -1
			}
		}

	private fun doesTokenPrecede(a: NFA, b: NFA) = precedenceTable[a]!! < precedenceTable[b]!!
}

data class Token<T>(
	val id: T,
	val value: String
)
