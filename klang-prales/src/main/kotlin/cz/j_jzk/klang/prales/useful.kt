package cz.j_jzk.klang.prales.useful

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.util.LinkedList

/**
 * Defines a node composed of a list of nodes of the specified ID.
 * The list can be empty.
 * For example, when the node type is an int and when whitespace is ignored,
 * the following is valid: `1 2 3`, `1`, ``.
 */
// TODO: automatically inherit ignored regexes from the parent
fun <T> list(node: NodeID<T>, ignoredRegexes: Array<String> = emptyArray()) = lesana<List<T>> {
    val list = NodeID<LinkedList<T>>()
    list to def(node, list) { (n, l) -> LinkedList.Node(n, l) }
    list to def() { LinkedList.Empty }

    val top = NodeID<List<T>>()
    top to def(list) { it.v1.toKotlinList() }
    setTopNode(top)
    ignoreRegexes(*ignoredRegexes)
}
