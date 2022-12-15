package cz.j_jzk.klang.prales.constants

import cz.j_jzk.klang.prales.testLesana
import kotlin.test.Test

class DecimalTest {
    @Test fun testDefaultConfiguration() {
        testLesana(
            decimal().getLesana(),
            mapOf(
                "12.34" to 12.34,
                "1.0" to 1.0,
                ".32" to 0.32,
            ),
            listOf(
                "1,2",
                "1.2.3",
                "12.",
            ),
        )
    }

    @Test fun testCustomSeparator() {
        testLesana(
            decimal(",").getLesana(),
            mapOf(
                "12,34" to 12.34,
                "1,0" to 1.0,
                ",32" to 0.32,
            ),
            listOf(
                "1.2",
                "1,2,3",
                "12,",
            ),
        )
    }

    @Test fun testDisallowEmptyIntegerPart() {
        testLesana(
            decimal(allowEmptyIntegerPart = false).getLesana(),
            mapOf(
                "12.34" to 12.34,
                "1.0" to 1.0,
            ),
            listOf(
                "1,2",
                "1.2.3",
                "12.",
                ".12",
            ),
        )
    }
}
