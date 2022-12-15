package cz.j_jzk.klang.prales.constants

import cz.j_jzk.klang.prales.testLesana
import kotlin.test.Test

class IntegerTest {
    @Test fun testDecimal() {
        testLesana(
            integer().getLesana(),
            mapOf(
                "0" to 0L,
                "1" to 1L,
                "99" to 99L,
                "123456789" to 123456789L,
            ),
            listOf(
                "-1",
                "0xff",
                "1+1",
                "1_1",
            ),
        )
    }

    @Test fun testNonDecimal() {
        testLesana(
            integer(true, false).getLesana(),
            mapOf(
                "0" to 0L,
                "123" to 123L,
                "0xff" to 255L,
                "0xfF" to 255L,
                "0o17" to 15L,
                "0b0101" to 5L,
            ),
            listOf(
                "-5",
                "0a123",
                "0xfg",
                "0b012",
                "0o8",
                "1_2",
                "0x1_2",
            ),
        )
    }

    @Test fun testUnderscoreSeparation() {
        testLesana(
            integer(false, true).getLesana(),
            mapOf(
                "0" to 0L,
                "123" to 123L,
                "123_456" to 123456L,
                "1_2_3" to 123L,
                "1__2" to 12L,
                "_12__" to 12L,
            ),
            listOf(
                "-5",
                "0x123",
                "0x1_2",
            ),
        )
    }

    @Test fun testNonDecimalWithUnderscoreSeparation() {
        testLesana(
            integer(true, true).getLesana(),
            mapOf(
                "0" to 0L,
                "123" to 123L,
                "123_456" to 123456L,
                "1_2_3" to 123L,
                "1__2" to 12L,
                "_12__" to 12L,
                "123" to 123L,
                "0xff" to 255L,
                "0xfF" to 255L,
                "0o17" to 15L,
                "0b0101" to 5L,
            ),
            listOf(
                "-5",
                "0x12_34",
            ),
        )
    }
}
