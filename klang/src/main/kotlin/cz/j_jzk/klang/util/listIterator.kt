package cz.j_jzk.klang.util.listiterator

/**
 * Returns a string before the cursor of the iterator.
 * @param len Length of the string
 */
internal fun ListIterator<Char>.previousString(len: Int): String {
	val builder = StringBuilder(len)
	// roll back the iterator
	for (i in 1..len)
		this.previous()
	// construct the string
	for (i in 1..len)
		builder.append(this.next())

	return builder.toString()
}

internal fun <T> ListIterator<T>.nextOrNull() =
	if (hasNext())
		next()
	else
		null

// TODO: move
/**
 * Skips over the items of the iterator until the predicate is true.file
 * @return The last value skipped (the one for which the predicate was true)
 */
internal inline fun <T> Iterator<T>.skipUntil(predicate: (T) -> Boolean): T? {
	for (item in this)
		if (predicate(item)) return item
	return null
}
