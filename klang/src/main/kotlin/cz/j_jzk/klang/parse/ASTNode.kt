package cz.j_jzk.klang.parse

import cz.j_jzk.klang.util.PositionInfo

/** This interface represents a node of the AST. */
// Do we need the type parameter N?
sealed interface ASTNode<out N> {
	val id: NodeID
	val position: PositionInfo

	/** A regular node of the AST. */
	data class Data<out N>(
		override val id: NodeID,
		val data: N,
		override val position: PositionInfo
	): ASTNode<N>

	/** A dummy AST node used for error recovery. */
	data class Erroneous<N>(
		override val id: NodeID,
		override val position: PositionInfo
	): ASTNode<N>

	/**
	 * An AST node that has no value (usually used for interoperability
	 * between the lexer and the parser)
	 */
	data class NoValue<N>(
		override val id: NodeID,
		override val position: PositionInfo
	): ASTNode<N>
}
