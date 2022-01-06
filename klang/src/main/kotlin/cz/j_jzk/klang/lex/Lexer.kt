package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.MultipleMatcher
import cz.j_jzk.klang.util.listiterator.previousString
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.input.IdentifiableInput
import java.io.EOFException

/**
 * The class that does the lexing. You probably don't want to create or use this
 * object directly, but instead use a builder (`cz.j_jzk.klang.lex.api.lexer()`),
 * which returns a LexerWrapper.
 * 
 * The type parameter `T` is the token type identifier. Enums are the most
 * suitable to this, but you may as well use anything you want.
 * 
 * The lexer isn't tied to an input stream, so you can use the same lexer object
 * to parse multiple inputs in parallel.
 */
class Lexer<T>(private val tokenDefs: LinkedHashMap<NFA, T>, private val ignored: List<NFA> = listOf()) {
	private val matcher = MultipleMatcher(tokenDefs.keys + ignored)
	private val precedenceTable: Map<NFA, Int>
	init {
		var i = 0
		precedenceTable = tokenDefs.map { (k, _) -> k to i++ }.toMap()
	}

	/**
	 * Match a token from an input.
	 * Returns `null` on no match.
	 * Throws an `EOFException` on EOF.
	 */
	fun nextToken(idInput: IdentifiableInput): Token<T>? {
		val input = idInput.input
		if (!input.hasNext())
			throw EOFException()

		val longestMatch = chooseMatch(matcher.nextMatch(input)) ?: return null

		if (longestMatch.key in ignored)
			return if (input.hasNext())
				nextToken(idInput)
			else
				null

		return Token(
			tokenDefs[longestMatch.key]!!,
			input.previousString(longestMatch.value),
			PositionInfo(idInput.id, input.previousIndex() - longestMatch.value + 1)
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
	val value: String,
	val position: PositionInfo,
)
