package cz.j_jzk.klang.testutils

import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.Token
import kotlin.test.Test
import kotlin.test.assertEquals

fun re(str: String) = compileRegex(str).fa
fun iter(str: String) = str.toList().listIterator()

fun testLex(lexer: Lexer<String>, input: String, expectedTokens: List<Token<String>>) {
    val inputIter = iter(input)
    for (token in expectedTokens) {
        assertEquals(token, lexer.nextToken(inputIter))
    }
}
