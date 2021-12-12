package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.MultipleMatcher
import cz.j_jzk.klang.util.listiterator.previousString

class Lexer<T>(val tokenDefs: Map<NFA, T>) {
	val matcher = MultipleMatcher(tokenDefs.keys)

	fun nextToken(input: ListIterator<Char>): Token<T>? {
		// TODO token precedence
		val longestMatch = matcher.nextMatch(input).maxByOrNull { (_, v) -> v }

		if (longestMatch == null)
			return null

		return Token(
			tokenDefs[longestMatch.key]!!,
			input.previousString(longestMatch.value)
		)
	}
}

data class Token<T>(
	val id: T,
	val value: String
)