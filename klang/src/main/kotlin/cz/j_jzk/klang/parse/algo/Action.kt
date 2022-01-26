package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode

// TODO better represenatation?
sealed interface Action {
	val nextState: State

	data class Shift(override val nextState: State): Action

	data class Reduce(
		val nNodes: Int,
		val reduction: (List<ASTNode>) -> ASTNode,
		override val nextState: State
	): Action
}
