package cz.j_jzk.klang.parse

import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.lex.Token

class ParserWrapper<I, D>(val dfa: DFA<D>, private val tokenConversions: Map<I, (String) -> D>) {
	fun parse(input: Iterator<Token<I>>): D {
		val result = dfa.parse(TokenConverter(tokenConversions, input))
		if (result is ASTNode.Erroneous)
			throw SyntaxError()
		else
			return (result as ASTNode.Data<D>).data
	}
}
