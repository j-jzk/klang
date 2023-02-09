package cz.j_jzk.klang.prales.useful

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.prales.testLesana
import kotlin.test.Test

class OptionalTest {
    @Test fun testOptional() {
        val lesana = lesana<String?> {
            setTopNode(include(optional(re("[0-9]"))))
        }.getLesana()

        testLesana(
            lesana,
            mapOf(
                "1" to "1",
                "2" to "2",
                "" to null,
            ),
            listOf("12")
        )
    }
}
