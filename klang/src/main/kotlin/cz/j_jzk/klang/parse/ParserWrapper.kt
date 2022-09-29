package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.algo.DFA

/** A utility class to simplify the interop between the lexer and the parser */
class ParserWrapper(val dfa: DFA, val tokenConversions: Map<Any, (String) -> Any>) {
	/**
	 * Parses the tokens from the `input`.
	 * @param input A stream of tokens, ideally from LexerWrapper.iterator()
	 * @throws SyntaxError if there were any syntax errors in the input
	 */
	// fun parse(input: LexerWrapper.LexerIterator) = parse(TokenConverter(tokenConversions, input))

	// /**
	//  * Parses the AST nodes from `input`.
	//  * @param input A stream of unprocessed AST nodes
	//  * @throws SyntaxError if there were any syntax errors in the input
	//  */
	// fun parse(input: Iterator<ASTNode>): Any {
	// 	val result = dfa.parse(input)
	// 	if (result is ASTNode.Erroneous)
	// 		throw SyntaxError()
	// 	else
	// 		return (result as ASTNode.Data).data
	// }
}
