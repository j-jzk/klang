package cz.j_jzk.klang.parse

import cz.j_jzk.klang.util.PositionInfo

/**
 * This interface represents a node of the Abstract Syntax Tree. It is used
 * internally by the parser and also passed to error-logging functions.
 */
// Do we need the type parameter N?
sealed interface ASTNode {
	/** The identifier of the node. */
	val id: NodeID<*>
	/** Information about the position of the node in the input text */
	val position: PositionInfo

	/** A regular node of the AST. */
	data class Data(
		override val id: NodeID<*>,
		val data: Any?,
		override val position: PositionInfo
	): ASTNode

	/** A dummy AST node used for error recovery. */
	data class Erroneous(
		override val id: NodeID<*>,
		override val position: PositionInfo
	): ASTNode

	/**
	 * An AST node that has no value (used historically, now used to handle EOF)
	 */
	data class NoValue(
		override val id: NodeID<*>,
		override val position: PositionInfo
	): ASTNode
}
