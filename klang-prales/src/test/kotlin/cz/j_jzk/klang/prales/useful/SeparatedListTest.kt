package cz.j_jzk.klang.prales.useful

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.testLesana
import kotlin.test.Test

class SeparatedListTest {
    @Test fun testWithoutTrailingSep() {
        val lesana = lesana<List<Int>> {
            val int = NodeID<Int>()
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val list = include(separatedList(int, re(","), allowTrailingSeparator=false))
            setTopNode(list)
        }.getLesana()

        testLesana(
            lesana,
            mapOf(
                "" to emptyList(),
                "1" to listOf(1),
                "1,2" to listOf(1, 2),
                "1,2,3,4" to listOf(1, 2, 3, 4),
            ),
            listOf(",", "1,", "1,,2", ",1", ",1,2"),
        )
    }

    @Test fun testWithTrailingSep() {
        val lesana = lesana<List<Int>> {
            val int = NodeID<Int>("int")
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val list = include(separatedList(int, re(","), allowTrailingSeparator=true))
            setTopNode(list)

            onUnexpectedToken(::println)
        }.getLesana()

        testLesana(
            lesana,
            mapOf(
                "" to emptyList(),
                "1" to listOf(1),
                "1,2" to listOf(1, 2),
                "1,2,3,4" to listOf(1, 2, 3, 4),
                "1," to listOf(1),
                "1,2," to listOf(1, 2),
                "1,2,3,4," to listOf(1, 2, 3, 4),
            ),
            listOf(",", "1,,2", ",1", ",1,2"),
        )
    }
}
