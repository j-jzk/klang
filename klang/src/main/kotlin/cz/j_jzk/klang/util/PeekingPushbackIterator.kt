package cz.j_jzk.klang.util

import org.apache.commons.collections4.iterators.PushbackIterator

// TODO: optimize
// TODO: test?
internal class PeekingPushbackIterator<T> private constructor(private val pushbackIt: PushbackIterator<T>): Iterator<T> by pushbackIt {
	public constructor(iterator: Iterator<T>): this(PushbackIterator.pushbackIterator(iterator))

	fun peek(): T = pushbackIt.next().also { pushbackIt.pushback(it) }
	fun peekOrNull(): T? = if (hasNext()) peek() else null
	fun pushback(item: T) = pushbackIt.pushback(item)
}
