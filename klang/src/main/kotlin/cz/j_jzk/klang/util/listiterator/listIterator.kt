package cz.j_jzk.klang.util.listiterator

/**
 * Returns a string before the cursor of the iterator.
 * @param len Length of the string
 */
fun ListIterator<Char>.previousString(len: Int): String {
	val builder = StringBuilder(len)
	// roll back the iterator
	for (i in 1..len)
		this.previous()
	// construct the string
	for (i in 1..len)
		builder.append(this.next())

	return builder.toString()
}