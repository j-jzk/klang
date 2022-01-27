package cz.j_jzk.klang.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest {
	@Test fun testPopTopString() {
		assertEquals("ef".toList(), "abcdef".toMutableList().popTop(2))
	}

	@Test fun testPopTopPops() {
		val list = "abcdef".toMutableList()
		list.popTop(2)
		assertEquals("abcd".toList(), list)
	}
}
