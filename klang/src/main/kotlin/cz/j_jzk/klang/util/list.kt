package cz.j_jzk.klang.util

/** Pops the last `nElemens` elements from a list and returns them in a list. */
internal fun <T> MutableList<T>.popTop(nElements: Int) =
	List(nElements) { this.removeLast() }
		.reversed()
