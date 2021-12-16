package cz.j_jzk.klang.lex.api

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer

/**
 * A function to create a lexer.
 * 
 * The type parameter `T` is the token type identifier. Enums are the most
 * suitable to this, but you may as well use anything you want.
 * 
 * Example:
 * ```
 * val lexerBuilder = lexer<TokenType> {
 *     // you use the keyword 'to' to assign token types to regexes
 *     TokenType.INT to "[0-9]+"
 *     // one token type may have multiple definitions
 *     TokenType.INT to "-[0-9]+"
 * }
 * // you could do something else with the lexer builder here
 * // ...
 * // To get the lexer:
 * val lexer = lexerBuilder.getLexer()
 * ```
 */
fun <T>lexer(init: LexerBuilder<T>.() -> Unit): LexerBuilder<T> {
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
	private val ignored = listOf<NFA>()

	infix fun T.to(b: String) {
		tokenDefs.set(compileRegex(b).fa, this)
	}

	fun getLexer(): Lexer<T> = Lexer(tokenDefs, ignored)
}
