package cz.j_jzk.klang.util

import kotlin.math.max

/**
 * A list that is optimised for search operations.
 * You create one using the static function SortedList.fromIterable().
 */
class SortedList<T: Comparable<T>> private constructor(private val sorted: List<T>): List<T> by sorted {
    override fun contains(element: T): Boolean = sorted.binarySearch(element) >= 0
    override fun indexOf(element: T): Int = max(sorted.binarySearch(element), -1)

    companion object {
        fun <T: Comparable<T>>fromIterable(unsorted: Iterable<T>): SortedList<T> = SortedList(unsorted.sorted())
    }
}