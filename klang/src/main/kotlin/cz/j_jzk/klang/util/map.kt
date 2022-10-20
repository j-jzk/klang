package cz.j_jzk.klang.util

import org.apache.commons.collections4.map.LazyMap

/**
 * Merges the source map into this map.
 */
internal fun <K, V> LazyMap<K, MutableSet<V>>.mergeSetValues(source: Map<K, Set<V>>) {
    for ((key, set) in source)
        this[key]!!.addAll(set)
}
