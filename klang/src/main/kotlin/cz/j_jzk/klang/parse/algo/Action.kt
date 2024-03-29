package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode

// TODO better represenatation?
/**
 * This class represents an action the parser can take at each step of the
 * parsing. It is intended for internal use only.
 */
sealed class Action {
	/** Shift an item from the input, push it onto the stack and go to `nextState`. */
	data class Shift(val nextState: State): Action()

	/**
	 * Pop `nNodes` from the stack, feed them into `reduction`, push the
	 * result onto the stack and go to `nextState`.
	 */
	data class Reduce(
		val nNodes: Int,
		val reduction: (List<ASTNode>) -> ASTNode,
	): Action()
}
