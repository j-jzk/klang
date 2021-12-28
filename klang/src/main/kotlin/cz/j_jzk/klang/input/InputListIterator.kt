package cz.j_jzk.klang.input

import cz.j_jzk.klang.util.list.pop
import java.io.InputStream

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
	private val forwardBuffer = mutableListOf<Char>()
	private val backwardBuffer = mutableListOf<Char>()
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
				forwardBuffer.pop()
			else
				input.read().toChar()

		backwardBuffer += value
		return value
	}

	override fun previous(): Char {
		index--

		val value = backwardBuffer.pop()
		forwardBuffer += value
		return value
	}

	override fun nextIndex() = index + 1
	override fun previousIndex() = index - 1
}