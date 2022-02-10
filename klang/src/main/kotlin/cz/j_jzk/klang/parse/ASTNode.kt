package cz.j_jzk.klang.parse

/** This interface represents a node of the AST. */
// Do we need the type parameter N?
sealed interface ASTNode<out N> {
	val id: NodeID

	/** A regular node of the AST. */
	data class Data<out N>(override val id: NodeID, val data: N): ASTNode<N>
	/** A dummy AST node used for error recovery. */
	data class Erroneous<N>(override val id: NodeID): ASTNode<N>
}
