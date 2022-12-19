package cz.j_jzk.klang.prales.useful

import kotlin.test.Test
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.testLesana

class ListTest {
    @Test fun testList() {
        testLesana(
            lesana<List<Int>> {
                val num = NodeID<Int>()
                num to def(re("[0-9]")) { it.v1.toInt() }
                setTopNode(include(list(num)))
            }.getLesana(),
            mapOf(
                "123" to listOf(1,2,3),
                "1" to listOf(1),
                "" to emptyList(),
            ),
            listOf("1 2"),
        )
    }
}
