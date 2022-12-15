package cz.j_jzk.klang.prales.constants

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

/**
 * Defines an identifier: [a-zA-Z_][a-zA-Z0-9_]*
 */
fun identifier() = lesana<String> {
    val identifier = NodeID<String>()
    identifier to def(re("[a-zA-Z_][a-zA-Z0-9_]*")) { it.v1 }
    setTopNode(identifier)
}

/**
 * Defines a positive integer constant.
 */
fun integer() = lesana<Int> {
    val integer = NodeID<Int>()
    integer to def(re("[0-9]+")) { it.v1.toInt() }
    setTopNode(integer)
}
