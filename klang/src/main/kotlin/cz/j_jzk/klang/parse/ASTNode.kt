package cz.j_jzk.klang.parse

/**
 * Represents a node of the AST.
 */
// Do we need the type parameter N?
sealed interface ASTNode<out N> {
	val id: NodeID

	data class Data<out N>(override val id: NodeID, val data: N): ASTNode<N>
	data class Errorneous<N>(override val id: NodeID): ASTNode<N>
}
