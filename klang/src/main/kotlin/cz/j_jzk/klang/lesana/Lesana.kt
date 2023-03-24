package cz.j_jzk.klang.lesana

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.LexerPPPIterator
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.input.IdentifiableInput

/**
 * Represents a **Le**xical-**s**emantic **ana**lyzer.
 * The main point of interaction when processing an input.
 *
 * You probably don't want to create this object directly, but instead use the
 * builder function [lesana].
 *
 * @constructor Creates a new lesana with the specified lexer and parser.
 */
class Lesana(val lexer: Lexer, val parser: DFA) {
	/** Parse the input, returning a raw AST node */
	fun parseRaw(input: IdentifiableInput): ASTNode =
		parser.parse(LexerPPPIterator(lexer, input))

	/**
	 * Parses the input according to the defined rules and returns the data in
	 * the form specified by the lesana definition.
	 */
	fun parse(input: IdentifiableInput): Any? {
		val result = parseRaw(input)
		if (result !is ASTNode.Data)
			throw SyntaxError()
		else
			return result.data
	}

	override fun toString(): String =
		"<Lesana(lexer=$lexer, parser=$parser)>"
}
