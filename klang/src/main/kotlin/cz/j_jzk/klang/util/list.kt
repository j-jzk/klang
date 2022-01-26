package cz.j_jzk.klang.util

fun <T> MutableList<T>.popTop(nElements: Int) = List(nElements) { this.removeLast() }
