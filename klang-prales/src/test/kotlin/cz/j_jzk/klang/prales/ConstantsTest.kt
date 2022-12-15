package cz.j_jzk.klang.prales.constants

import kotlin.test.Test

class ConstantsTest {
    @Test fun testIdentifier() {
        testLesana(
            identifier().getLesana(),
            mapOf(
                "identifier" to "identifier",
                "ClassName" to "ClassName",
                "i" to "i",
                "_" to "_",
                "_HeLlO123" to "_HeLlO123",
                "num1ber" to "num1ber",
            ),
            listOf(
                "white space",
                "1number",
                "2*3",
                "a-b",
            ),
        )
    }

    @Test fun testInteger() {
        testLesana(
            integer().getLesana(),
            mapOf(
                "0" to 0,
                "1" to 1,
                "123456789" to 123456789,
            ),
            listOf(
                "-1",
                "0xff",
                "1+1",
            )
        )
    }
}
