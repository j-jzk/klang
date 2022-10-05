package cz.j_jzk.klang.testutil

/**
 * Essentialy the same as PushbackIterator from Apache Commons, but allows null
 * values since it doesn't use ArrayDeque
 */
@Suppress("IteratorNotThrowingNoSuchElementException")
class PushbackIterator<T>(val iterator: Iterator<T>): Iterator<T> {
    private val pushbackBuffer = mutableListOf<T>()

    override fun hasNext() = pushbackBuffer.isNotEmpty() || iterator.hasNext()
    override fun next(): T =
        if (pushbackBuffer.isNotEmpty())
            pushbackBuffer.removeLast()
        else
            iterator.next()

    fun pushback(element: T) = pushbackBuffer.add(element)
}
