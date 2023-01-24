package cz.j_jzk.klang.parse

/**
 * An AST node ID. All node IDs must extend this class.
 *
 * @param D represents the data type that will be stored in nodes with this ID.
 * @property name An optional human-readable name, used in error messages
 */
open class NodeID<out D>(val name: String? = null) {
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
