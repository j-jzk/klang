package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.LexerPPPIterator
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.input.IdentifiableInput

class Sele(val lexer: LexerWrapper, val parser: DFA) {
	/** Parse the input, returning a raw ASTNode */
	fun parseRaw(input: IdentifiableInput): ASTNode =
		parser.parse(LexerPPPIterator(lexer, input))

	fun parse(input: IdentifiableInput): Any? {
		val result = parseRaw(input)
		if (result !is ASTNode.Data)
			throw SyntaxError()
		else
			return result.data
	}
}
