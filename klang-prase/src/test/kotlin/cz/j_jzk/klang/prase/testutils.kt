package cz.j_jzk.klang.prase

import cz.j_jzk.klang.sele.Sele
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.parse.SyntaxError
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun testSele(sele: Sele, shouldMatch: Map<String, Any?>, shouldNotMatch: Collection<String>) {
    for ((input, result) in shouldMatch) {
        assertEquals(result, sele.parse(InputFactory.fromString(input, "")))
    }

    for (input in shouldNotMatch) {
        assertFailsWith<SyntaxError>("Expected syntax error for input '$input'") {
            sele.parse(InputFactory.fromString(input, ""))
        }
    }
}
