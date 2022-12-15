package cz.j_jzk.klang.prales.util

internal sealed class LinkedList<out T> {
    fun toKotlinList(): List<T> {
        val result = mutableListOf<T>()
        var node = this
        while (node is Node) {
            result.add(node.v)
            node = node.next
        }
        return result
    }

    data class Node<out T>(val v: T, val next: LinkedList<T>): LinkedList<T>()
    object Empty: LinkedList<Nothing>()
}
