package cz.j_jzk.klang.testutils

import cz.j_jzk.klang.input.IdentifiableInput
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.Token
import kotlin.test.assertEquals

fun re(str: String) = compileRegex(str).fa
fun iter(str: String) = InputFactory.fromString(str, "id")

fun testLex(lexer: Lexer, input: String, expectedTokens: List<FToken>) {
    val inputIter = InputFactory.fromString(input, "whatever")
    for (token in expectedTokens) {
        assertEquals<Any?>(token, lexer.nextToken(inputIter))
    }
}

fun testLexWithPositions(lexer: Lexer, input: IdentifiableInput, expectedTokens: List<Token>) {
    for (token in expectedTokens) {
        assertEquals(token, lexer.nextToken(input))
    }
}

/**
 * A fake token class for when you don't care about the position info
 */
data class FToken(
    val id: String,
    val value: String
) {
    override fun equals(other: Any?) =
        if (other is FToken) {
            this.id == other.id && this.value == other.value
        } else if (other is Token && other.id is String) {
            this.id == other.id && this.value == other.value
        } else false
}
