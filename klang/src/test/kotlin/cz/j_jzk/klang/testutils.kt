package cz.j_jzk.klang.testutils

import cz.j_jzk.klang.input.IdentifiableInput
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.Token
import cz.j_jzk.klang.lex.api.AnyNodeID
import cz.j_jzk.klang.parse.NodeID
import kotlin.test.assertEquals

fun re(str: String) = compileRegex(str).fa
fun iter(str: String) = InputFactory.fromString(str, "id")

fun testLex(lexer: Lexer, input: String, expectedTokens: List<FToken>, ignore: Collection<String> = emptyList()) {
    val inputIter = InputFactory.fromString(input, "whatever")
    val ignoreRe = ignore.map { compileRegex(it).fa }
    for (token in expectedTokens) {
        assertEquals<Any?>(token, lexer.nextToken(inputIter, lexer.registeredTokenTypes, ignoreRe))
    }
}

fun testLexWithPositions(
    lexer: Lexer,
    input: IdentifiableInput,
    expectedTokens: List<Token>,
    ignore: Collection<String> = emptyList()
) {
    val ignoreRe = ignore.map { compileRegex(it).fa }
    for (token in expectedTokens) {
        assertEquals(token, lexer.nextToken(input, lexer.registeredTokenTypes, ignoreRe))
    }
}

/**
 * A fake token class for when you don't care about the position info
 */
data class FToken(
    val id: NodeID<*>,
    val value: String
) {
    constructor(id: String, value: String): this(AnyNodeID(id), value)

    override fun equals(other: Any?) =
        if (other is Token)
            this.id == other.id && this.value == other.value
        else false
}
