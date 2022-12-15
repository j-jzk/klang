package cz.j_jzk.klang.prase

import kotlin.test.Test

class IdentifierTest {
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
}
