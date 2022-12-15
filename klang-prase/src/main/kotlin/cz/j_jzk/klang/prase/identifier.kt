package cz.j_jzk.klang.prase

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
