package cz.j_jzk.klang.prales

import cz.j_jzk.klang.lesana.Lesana
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.parse.SyntaxError
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun testLesana(lesana: Lesana, shouldMatch: Map<String, Any?>, shouldNotMatch: Collection<String>) {
    for ((input, result) in shouldMatch) {
        try {
            assertEquals(result, lesana.parse(InputFactory.fromString(input, "")))
        } catch (_: SyntaxError) {
            throw AssertionError("Syntax error for input '$input'")
        }
    }

    for (input in shouldNotMatch) {
        assertFailsWith<SyntaxError>("Expected syntax error for input '$input'") {
            lesana.parse(InputFactory.fromString(input, ""))
        }
    }
}
