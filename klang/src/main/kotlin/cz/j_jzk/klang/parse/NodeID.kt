package cz.j_jzk.klang.parse

/**
 * An AST node ID. All node IDs must extend this class.
 * The type parameter D represents the data type that will be stored
 * in nodes with this ID.
 */
open class NodeID<out D>

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
