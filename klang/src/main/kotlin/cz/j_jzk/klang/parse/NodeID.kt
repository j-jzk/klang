package cz.j_jzk.klang.parse

/**
 * A class used internally to identify nodes of the AST.
 */
sealed class NodeID {
	/** An identifier supplied by the user */
	data class ID(val id: Any): NodeID()
	/** End of input */
	object Eof: NodeID()
}
