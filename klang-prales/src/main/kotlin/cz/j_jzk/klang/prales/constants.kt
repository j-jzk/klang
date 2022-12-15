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
 * Defines a positive integer constant, e.g. 123
 *
 * @param nonDecimal whether to allow the binary, octal and hexadecimal number systems.
 *  When true, the following is legal: 0b01101, 0o321, 0xff, 0xFF
 * @param underscoreSeparation whether to allow underscore separation - only in decimal constants
 *  When true, the following is legal: 123_456, 1_2_3, 1__2, _1_
 */
fun integer(nonDecimal: Boolean = false, underscoreSeparation: Boolean = false) = lesana<Long> {
    val integer = NodeID<Long>()

    if (underscoreSeparation)
        integer to def(re("[0-9_]+")) { it.v1.replace("_", "").toLong() }
    else
        integer to def(re("[0-9]+")) { it.v1.toLong() }

    if (nonDecimal) {
        integer to def(re("0b[01]+")) { it.v1.substring(2).toLong(2) }
        integer to def(re("0o[0-7]+")) { it.v1.substring(2).toLong(8) }
        integer to def(re("0x[0-9a-fA-F]+")) { it.v1.substring(2).toLong(16) }
    }

    val top = NodeID<Long>()
    top to def(integer) { it.v1 }
    setTopNode(top)
}
