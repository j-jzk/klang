package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode

// TODO better represenatation?
/**
 * This class represents an action the parser can take at each step of the
 * parsing. It is intended for internal use only.
 */
sealed class Action<out N> {
	/** Shift an item from the input, push it onto the stack and go to `nextState`. */
	data class Shift(val nextState: State): Action<Nothing>()

	/**
	 * Pop `nNodes` from the stack, feed them into `reduction`, push the
	 * result onto the stack and go to `nextState`.
	 */
	data class Reduce<N>(
		val nNodes: Int,
		val reduction: (List<ASTNode<N>>) -> ASTNode<N>,
	): Action<N>()
}
