package cz.j_jzk.klang.prales.useful

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.util.LinkedList

/**
 * Defines a node composed of a list of nodes of the specified ID.
 * The list can be empty.
 *
 * This lesana automatically inherits ignored regexes from the parent. This
 * can be turned off using the parameter `inheritIgnores`.
 *
 * For example, when the node type is an int and when whitespace is ignored,
 * the following is valid: `1 2 3`, `1`, ``.
 */
fun <T> list(node: NodeID<T>, inheritIgnores: Boolean = true) = lesana<List<T>> {
    val list = NodeID<LinkedList<T>>("list($node)")
    list to def(node, list) { (n, l) -> LinkedList.Node(n, l) }
    list to def() { LinkedList.Empty }

    val top = NodeID<List<T>>()
    top to def(list) { it.v1.toKotlinList() }
    setTopNode(top)

    if (inheritIgnores)
        inheritIgnoredREs()
}

fun <T> optional(node: NodeID<T>) = lesana<T?> {
    val optionalNode = NodeID<T?>("optional($node)")
    optionalNode to def(node) { it.v1 }
    optionalNode to def() { null }

    setTopNode(optionalNode)
}

fun <T> separatedList(node: NodeID<T>, separator: NodeID<*>, allowTrailingSeparator: Boolean = true, inheritIgnores: Boolean = true) = lesana<List<T>> {
    val list = NodeID<LinkedList<T>>("separated list($node)")
    val nonEmptyList = NodeID<LinkedList<T>>(show=false)

    list to def() { LinkedList.Empty }

    if (allowTrailingSeparator) {
        list to def(nonEmptyList, include(optional(separator))) { (l, _) -> l }
    } else {
        list to def(nonEmptyList) { (l) -> l }
    }

    nonEmptyList to def(node) { (n) -> LinkedList.Node(n, LinkedList.Empty) }
    nonEmptyList to def(nonEmptyList, separator, node) { (l, _, n) -> LinkedList.Node(n, l) }

    val top = NodeID<List<T>>(show=false)
    top to def(list) { it.v1.toKotlinList() }
    setTopNode(top)

    if (inheritIgnores)
        inheritIgnoredREs()
}

/**
 * Defines a possibly escaped character, without double or single quotes.
 * Allowed values:
 *  . - a literal character
 *  \n, \t, \b, \r, \\ - an escape sequence with a defined meaning
 *  \uXXXX - an UTF-16 encoded character, where XXXX is the character's
 *           codeword in hexadecimal
 *
 * @param specialCharacters - defines a list of special characters. They aren't
 *  allowed as literal characters, but they can appear as escape sequences.
 *  Special care must be taken because this string is inserted directly into
 *  regexes, so it can't contain special characters. They are used in addition
 *  to the predefined ones.
 */
internal fun rawCharacter(specialCharacters: String = "\\\\") = lesana<Char> {
    val char = NodeID<Char>("character")

    char to def(re("[^$specialCharacters]")) { it.v1[0] }

    val escapes = mapOf("\\n" to '\n', "\\t" to '\t', "\\b" to '\b', "\\r" to '\r', "\\\\" to '\\')
    char to def(re("\\\\[ntbr\\\\]")) { escapes[it.v1] }

    char to def(re("\\\\[$specialCharacters]")) { it.v1[1] }

    char to def(re("""\\u\x\x\x\x""")) { (escaped) ->
        String(
            escaped
                .substring(2) // strip \u
                .chunked(2)
                .map { it.toByte(16) }
                .toByteArray(),
            Charsets.UTF_16,
        )[0]
    }

    setTopNode(char)
}
