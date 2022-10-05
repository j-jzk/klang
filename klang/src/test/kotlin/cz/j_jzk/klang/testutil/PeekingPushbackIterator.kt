package cz.j_jzk.klang.testutil

import kotlin.math.max

@Suppress("IteratorNotThrowingNoSuchElementException")
class PeekingPushbackIterator<T> private constructor(
	private val pushbackIt: PushbackIterator<T>
): Iterator<T> {
	public constructor(iterator: Iterator<T>): this(PushbackIterator(iterator))

	private var pushbackCount = 0

	override fun hasNext() = pushbackIt.hasNext()
	override fun next() = pushbackIt.next().also { pushbackCount = max(pushbackCount-1, 0) }
	fun peek(): T = pushbackIt.next().also { pushbackIt.pushback(it) }
	fun peekOrNull(): T? = if (hasNext()) peek() else null
	fun pushback(item: T) = pushbackIt.pushback(item).also { pushbackCount++ }
	val hasItemsInPushbackBuffer
		get() = pushbackCount > 0
}
