package cz.j_jzk.klang.util

internal fun <T> MutableList<T>.popTop(nElements: Int) =
	List(nElements) { this.removeLast() }
		.reversed()
