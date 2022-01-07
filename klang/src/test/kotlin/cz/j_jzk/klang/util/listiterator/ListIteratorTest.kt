package cz.j_jzk.klang.util.listiterator

import kotlin.test.Test
import kotlin.test.assertEquals

class ListIteratorTest {
	@Test fun testPreviousStringOneCharacter() {
		val iterator = newIter()
		iterator.next()

		assertEquals("q", iterator.previousString(1))
	}

	@Test fun testPreviousStringMultipleCharacters() {
		val iterator = newIter()
		iterator.next()
		iterator.next()

		assertEquals("qw", iterator.previousString(2))
	}

	@Test fun testPreviousStringInTheMiddle() {
		val iterator = newIter()
		iterator.next()
		iterator.next()
		iterator.next()

		assertEquals("we", iterator.previousString(2))
	}

	@Test fun testPreviousStringRewindsCorrectly() {
		val iterator = newIter()
		iterator.next()
		iterator.previousString(1)

		assertEquals('w', iterator.next())
	}

	private fun newIter() = "qwertyuiop".toList().listIterator()
}
