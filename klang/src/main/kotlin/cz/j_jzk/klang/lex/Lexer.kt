package cz.j_jzk.klang.lex

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.nextMatchFromMultiple
import cz.j_jzk.klang.util.listiterator.previousString
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.input.IdentifiableInput
import cz.j_jzk.klang.parse.NodeID
import java.io.EOFException
import com.google.common.collect.Multimaps
import com.google.common.collect.ArrayListMultimap
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.parse.UnexpectedCharacter

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
class Lexer(regexToId: LinkedHashMap<NFA, NodeID<*>>) {
	// we need to add a catch-all definition for error recovery
	private val unexpectedCharDef = compileRegex(".").fa to UnexpectedCharacter
	private val regexToId = regexToId + unexpectedCharDef
	private val idToRegex = Multimaps.invertFrom(Multimaps.forMap(this.regexToId), ArrayListMultimap.create())

	/** All the token IDs known to this lexer */
	val registeredTokenTypes = this.regexToId.values

	private val allNFAs = this.regexToId.keys
	private val precedenceTable: Map<NFA, Int>
	init {
		var i = 0
		precedenceTable = this.regexToId.map { (k, _) -> k to i++ }.toMap()
	}

	/**
	 * Match a token from an input.
	 * Returns `null` on no match.
	 * Throws an `EOFException` on EOF.
	 */
	fun nextToken(
		idInput: IdentifiableInput,
		expectedTokenTypes: Collection<NodeID<*>> = regexToId.values,
		ignored: Collection<NFA> = emptyList()
	): Token? {
		val input = idInput.input
		if (!input.hasNext())
			throw EOFException()

		// TODO: we should make sure that there aren't more IDs assigned to one RE
		// (it is acceptable as long as they won't be expected simultaneously)
		// (or maybe we should merge equal REs into one ID)
		val nfas = expectedTokenTypes.map { idToRegex[it] }.flatten() + ignored + unexpectedCharDef.first
		val longestMatch = chooseMatch(nextMatchFromMultiple(nfas, input)) ?: return null

		if (longestMatch.key in ignored)
			return if (input.hasNext())
				nextToken(idInput, expectedTokenTypes, ignored)
			else
				null

		return Token(
			regexToId[longestMatch.key]!!,
			input.previousString(longestMatch.value),
			PositionInfo(idInput.id, input.previousIndex() - longestMatch.value + 1)
		)
	}

	/**
	 * Chooses a token according to maximal-munch and precedence of tokens
	 */
	private fun chooseMatch(matches: Map<NFA, Int>): Map.Entry<NFA, Int>? =
		matches.maxWithOrNull { a, b ->
			when {
				// maximal munch
				a.value > b.value -> 1
				// the unexpected character def should always have the lowest precedence
				a.key === unexpectedCharDef.first -> -1
				b.key === unexpectedCharDef.first -> 1
				// token precedence
				a.value == b.value && doesTokenPrecede(a.key, b.key) -> 1
				a === b -> 0
				else -> -1
			}
		}

	private fun doesTokenPrecede(a: NFA, b: NFA): Boolean =
		(precedenceTable[a] ?: Int.MAX_VALUE) < (precedenceTable[b] ?: Int.MIN_VALUE)

	override fun toString(): String = "Lexer(NFA -> ID = $regexToId)"
}

@Suppress("UndocumentedPublicClass")
data class Token(
	val id: NodeID<*>,
	val value: String,
	val position: PositionInfo,
)
