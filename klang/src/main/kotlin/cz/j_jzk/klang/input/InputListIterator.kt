package cz.j_jzk.klang.input

import java.io.InputStream
import java.util.LinkedList

/**
 * This class is a [ListIterator] implementation that gets its elements from a
 * [java.io.InputStream].
 *
 * ListIterator is used to supply characters to the lesana (specifically, the lexer),
 * and this class makes it possible to create a ListIterator from an InputStream such
 * as STDIN or a file.
 *
 * Several factory functions for the usual types of input are in the [InputFactory]
 * object.
 */
class InputListIterator(val input: InputStream): ListIterator<Char> {
	/* Buffers for moving backwards and forwards.
	 *
	 * When moving forwards, the forwardBuffer is filled with characters from
	 * the input stream as needed.
	 *
	 * It is quite inefficient to pop from one list and push to the other one.
	 * It would be better to instead have just a node of a doubly linked list
	 * and use it to shift backward and forward, but the standard implementation
	 * of linked list in Java doesn't provide a node class.
	 */
	private val forwardBuffer = LinkedList<Char>()
	private val backwardBuffer = LinkedList<Char>()
	private var index = 0

	override fun hasPrevious() = !backwardBuffer.isEmpty()
	override fun hasNext(): Boolean {
		if (!forwardBuffer.isEmpty())
			return true

		val nextFromInput = input.read()
		// input.read() returns -1 on end of input
		if (nextFromInput != -1) {
			forwardBuffer += nextFromInput.toChar()
			return true
		}

		return false
	}

	override fun next(): Char {
		index++

		val value = if (!forwardBuffer.isEmpty())
				forwardBuffer.removeLast()
			else
				input.read().toChar()

		backwardBuffer += value
		return value
	}

	override fun previous(): Char {
		index--

		val value = backwardBuffer.removeLast()
		forwardBuffer += value
		return value
	}

	override fun nextIndex() = index
	override fun previousIndex() = index - 1
}
