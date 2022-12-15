package cz.j_jzk.klang.prales.util

import kotlin.test.Test
import kotlin.test.assertEquals

class LinkedListTest {
    @Test fun testToKotlinList() {
        val list: LinkedList<Int> = LinkedList.Node(
            1,
            LinkedList.Node(2,
            LinkedList.Node(3,
            LinkedList.Empty))
        )

        assertEquals(listOf(1, 2, 3), list.toKotlinList())
    }

    @Test fun testEmptyList() {
        assertEquals(emptyList(), LinkedList.Empty.toKotlinList())
    }
}
