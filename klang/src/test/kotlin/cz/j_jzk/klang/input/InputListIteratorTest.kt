package cz.j_jzk.klang.input

import java.io.InputStream
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class InputListIteratorTest {
	@Test fun testForwardIteration() {
		val iterator = newIterator()
		for (c in "123456789")
			assertEquals(c, iterator.next())
	}

	@Test fun testBackwardIteration() {
		val iterator = newIterator()

		// skip over all of the chars
		for (c in "123456789")
			iterator.next()

		for (c in "987654321")
			assertEquals(c, iterator.previous())
	}

	@Test fun testHasNext() {
		val iterator = newIterator()
		assertTrue(iterator.hasNext())

		for (c in "123456789") {
			assertTrue(iterator.hasNext())
			iterator.next()
		}

		assertFalse(iterator.hasNext())
		
		iterator.previous()
		assertTrue(iterator.hasNext())
	}

	@Test fun testHasPrevious() {
		val iterator = newIterator()
		assertFalse(iterator.hasPrevious())

		iterator.next()
		assertTrue(iterator.hasPrevious())
		iterator.previous()
		assertFalse(iterator.hasPrevious())

		for (c in "123456789") {
			iterator.next()
			assertTrue(iterator.hasPrevious())
		}
	}

	@Test fun testNextIndex() {
		val iterator = newIterator()
		assertEquals(0, iterator.nextIndex())
		assertEquals(0, iterator.nextIndex())

		iterator.next()
		assertEquals(1, iterator.nextIndex())
		iterator.previous()
		assertEquals(0, iterator.nextIndex())
	}

	@Test fun testPreviousIndex() {
		val iterator = newIterator()
		iterator.next()
		assertEquals(0, iterator.previousIndex())
		assertEquals(0, iterator.previousIndex())

		iterator.next()
		assertEquals(1, iterator.previousIndex())
		iterator.previous()
		assertEquals(0, iterator.previousIndex())
	}

	@Test fun testHasFunctionsDontMutateData() {
		val iterator = newIterator()

		iterator.hasNext()
		assertEquals('1', iterator.next())
		iterator.hasPrevious()
		assertEquals('2', iterator.next())
	}

	@Test fun testIndexFunctionsDontMutateData() {
		val iterator = newIterator()

		iterator.nextIndex()
		assertEquals('1', iterator.next())
		iterator.previousIndex()
		assertEquals('2', iterator.next())
	}

	private fun newIterator() = InputListIterator(ByteArrayInputStream("12345789".toByteArray()))
}