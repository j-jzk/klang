package cz.j_jzk.klang.lex.api

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.util.PositionInfo

/**
 * A function to create a lexer.
 *
 * The type parameter `T` is the token type identifier. Enums are the most
 * suitable for this, but you may as well use anything you want.
 *
 * Example:
 * ```
 * val lexerBuilder = lexer<TokenType> {
 *     // you use the keyword 'to' to assign token types to regexes
 *     TokenType.INT to "[0-9]+"
 *     // one token type may have multiple definitions
 *     TokenType.INT to "-[0-9]+"
 *     // you can ignore tokens (e.g. whitespace, comments)
 *     ignore("\\s", "#[^\\n]")
 *     // the action to be called when an unexpected character is encountered
 *     onNoMatch { char, position ->
 *         // the character will be automatically skipped
 *         println("Unexpected character $char at $position")
 *     }
 * }
 * // you could do something else with the lexer builder here
 * // ...
 * // To get the lexer:
 * val lexer = lexerBuilder.getLexer()
 * ```
 */
fun <T> lexer(init: LexerBuilder<T>.() -> Unit): LexerBuilder<T> {
	val builder = LexerBuilder<T>()
	builder.init()
	return builder
}

/**
 * A lexer builder. You probably don't want to create this directly, but
 * instead use the function lexer() from this package.
 */
class LexerBuilder<T> {
	private val tokenDefs = linkedMapOf<NFA, T>()
	private val ignored = mutableListOf<NFA>()
	private var onNoMatchHandler: ((Char, PositionInfo) -> Unit)? = null

	/**
	 * Binds a token type to a regular expression.
	 * See https://github.com/j-jzk/klang-re for a reference of the supported syntax.
	 */
	infix fun T.to(b: String) {
		tokenDefs.set(compileRegex(b).fa, this)
	}

	/**
	 * Declares some tokens to be ignored (e.g. comments, whitespace etc.)
	 */
	fun ignore(vararg regex: String) {
		ignored.addAll(regex.map { compileRegex(it).fa })
	}

	/**
	 * Declares an action that will be executed when the lexer encounters an
	 * unexpected character. The character will be automatically skipped and
	 * it will be passed as the parameter of the lambda.
	 *
	 * Having multiple onNoMatch blocks is prohibited.
	 *
	 * @param lambda The action which will be called with the unexpected character and its PositionInfo as the parameter.
	 */
	fun onNoMatch(lambda: (Char, PositionInfo) -> Unit) {
		check(onNoMatchHandler == null) { "Having multiple onNoMatch blocks is prohibited." }
		onNoMatchHandler = lambda
	}

	/** Builds the lexer. */
	fun getLexer() = LexerWrapper(Lexer(tokenDefs, ignored), onNoMatchHandler ?: { _, _ -> })
}
