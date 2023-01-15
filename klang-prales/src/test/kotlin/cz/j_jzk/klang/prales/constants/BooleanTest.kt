package cz.j_jzk.klang.prales.constants

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana

class BooleanTest {
    @Test fun testBasic() {
        testLesana(
            boolean().getLesana(),
            mapOf(
                "true" to true,
                "false" to false,
            ),
            listOf("", " ", "TrUe", "False"),
        )
    }
}
