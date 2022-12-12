package cz.j_jzk.klang.util

import org.apache.commons.collections4.map.LazyMap

/**
 * Merges the source map into this map.
 */
internal fun <K, V> LazyMap<K, MutableSet<V>>.mergeSetValues(source: Map<K, Set<V>>) {
    for ((key, set) in source)
        this[key]!!.addAll(set)
}

internal operator fun <K, V> LinkedHashMap<K, V>.plus(element: Pair<K, V>): LinkedHashMap<K, V> {
    val result = LinkedHashMap<K, V>()
    result.putAll(this)
    result[element.first] = element.second
    return result
}
