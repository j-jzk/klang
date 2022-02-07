package cz.j_jzk.klang.parse

/**
 * A class used internally to identify nodes of the AST.
 */
sealed class NodeID {
	/** An identifier supplied by the user */
	data class ID<I>(val id: I): NodeID()
	/** End of input */
	object Eof: NodeID()
}
