package cz.j_jzk.klang.lex.api

import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer

fun <T>lexer(init: LexerBuilder<T>.() -> Unit): LexerBuilder<T> {
	val builder = LexerBuilder<T>()
	builder.init()
	return builder
}

class LexerBuilder<T> {
	private val tokenDefs = linkedMapOf<NFA, T>()

	infix fun T.to(b: String) {
		tokenDefs.set(compileRegex(b).fa, this)
	}

	fun getLexer(): Lexer<T> = Lexer(tokenDefs)
}
