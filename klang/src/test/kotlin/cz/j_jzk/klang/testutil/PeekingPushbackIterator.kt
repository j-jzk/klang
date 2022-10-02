package cz.j_jzk.klang.testutil

class PeekingPushbackIterator<T> private constructor(
	private val pushbackIt: PushbackIterator<T>
): Iterator<T> by pushbackIt {
	public constructor(iterator: Iterator<T>): this(PushbackIterator(iterator))

	fun peek(): T = pushbackIt.next().also { pushbackIt.pushback(it) }
	fun peekOrNull(): T? = if (hasNext()) peek() else null
	fun pushback(item: T) = pushbackIt.pushback(item)
}
