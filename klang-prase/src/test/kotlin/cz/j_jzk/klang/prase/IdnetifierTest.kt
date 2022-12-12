package cz.j_jzk.klang.prase

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class IdnetifierTest {
    @Test fun testIdentifier() {
        testSele(
            identifier().getSele(),
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
