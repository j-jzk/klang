package cz.j_jzk.klang.parse

/**
 * An AST node ID. All node IDs must extend this class (or be instances of it).
 *
 * @param D represents the data type that will be stored in nodes with this ID.
 * @property name An optional human-readable name, used in error messages.
 *  If it is null, a default name is used (like NodeID@8d82504)
 * @property show If false, the NodeID isn't shown in the list of expected
 *  tokens in error messages. Hiding NodeIDs that don't carry any informational
 *  value helps unclutter error messages.
 */
open class NodeID<out D>(val name: String? = null, val show: Boolean = true) {
    override fun toString() = name ?: super.toString()
}

/**
 * A NodeID representing EOF
 */
object EOFNodeID: NodeID<Nothing>() {
    override fun toString() = "EOF"
}

/**
 * A special NodeID representing an unexpected character in the input.
 */
object UnexpectedCharacter: NodeID<String>()
