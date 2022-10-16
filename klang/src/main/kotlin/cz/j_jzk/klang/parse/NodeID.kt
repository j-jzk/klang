package cz.j_jzk.klang.parse

/**
 * Used for semantics anywhere a node ID is expected.
 */
typealias NodeID = Any

/**
 * A NodeID representing EOF
 */
object EOFNodeID: NodeID() {
    override fun toString() = "EOF"
}
