package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.LexerPPPIterator
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.input.IdentifiableInput

/**
 * Represents a semantic-lexical analyzer.
 * The main point of interaction when processing an input.
 */
class Sele(val lexer: LexerWrapper, val parser: DFA) {
	/** Parse the input, returning a raw ASTNode */
	fun parseRaw(input: IdentifiableInput): ASTNode =
		parser.parse(LexerPPPIterator(lexer, input))

	/**
	 * Parses the input according to the defined rules and returns the data in
	 * the form specified by the sele definition.
	 */
	fun parse(input: IdentifiableInput): Any? {
		val result = parseRaw(input)
		if (result !is ASTNode.Data)
			throw SyntaxError()
		else
			return result.data
	}
}
