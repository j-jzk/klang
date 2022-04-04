package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.lex.LexerWrapper

/** A utility class to simplify the interop between the lexer and the parser */
class ParserWrapper<I, D>(val dfa: DFA<D>, val tokenConversions: Map<I, (String) -> D>) {
	/**
	 * Parses the tokens from the `input`.
	 * @param input A stream of tokens, ideally from LexerWrapper.iterator()
	 * @throws SyntaxError if there were any syntax errors in the input
	 */
	fun parse(input: LexerWrapper<I>.LexerIterator) = parse(TokenConverter(tokenConversions, input))

	/**
	 * Parses the AST nodes from `input`.
	 * @param input A stream of unprocessed AST nodes
	 * @throws SyntaxError if there were any syntax errors in the input
	 */
	fun parse(input: Iterator<ASTNode<D>>): D {
		val result = dfa.parse(input)
		if (result is ASTNode.Erroneous)
			throw SyntaxError()
		else
			return (result as ASTNode.Data<D>).data
	}
}
