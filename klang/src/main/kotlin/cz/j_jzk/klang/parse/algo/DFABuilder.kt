package cz.j_jzk.klang.parse.algo

import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.set
import java.util.ArrayDeque

internal data class LR1Item<N>(
	val nodeDef: NodeDef<N>,
	val dotBefore: Int, // which element of the def is the dot before
	val sigma: Set<NodeID>,
)

/**
 * A convenience function for getting the element of the item after the dot.
 * If the dot is at the end, this returns null.
 */
private fun <N> LR1Item<N>.elementAfterDot(): NodeID? =
	nodeDef.elements.getOrNull(dotBefore)

/**
 * This class builds a parser from the formal grammar.
 */
// TODO: precompute & memoize everything we can
class DFABuilder<N>(
	/** The formal grammar */
	val nodeDefs: Map<NodeID, Set<NodeDef<N>>>,

	/**
	 * The top node of the grammar. It needs to have a single definition, not
	 * ending in EOF.
	 */
	val topNode: NodeID,

	/**
	 * The error-recovering nodes (nodes which will be used to contain syntax
	 * errors)
	 */
	val errorRecoveringNodes: List<NodeID>,

	/**
	 * A callback used when the parser encounters a syntax error.
	 */
	val onError: (ASTNode<N>) -> Unit,
) {
	private val transitions = HashBasedTable.create<State, NodeID, Action<N>>()

	/**
	 * This variable maps the states as seen by the builder to the states seen
	 * by the DFA
	 */
	private val constructorStates = mutableMapOf<Set<LR1Item<N>>, State>()

	private val stateFactory = StateFactory()

	/** This function constructs the parser and returns it. */
	fun build(): DFA<N> {
		val topNodeDef = nodeDefs[topNode]!!.first()
		var startingSet = mutableSetOf(
			LR1Item(topNodeDef, 0, setOf(NodeID.Eof))
		)
		val startState = stateFactory.new(isErrorRecovering(startingSet))

		constructorStates[startingSet] = startState
		constructStates(startingSet, startState)

		return DFA(transitions, topNode, startState, errorRecoveringNodes, onError)
	}

	private fun constructStates(itemSet: MutableSet<LR1Item<N>>, thisState: State) {
		epsilonClosure(itemSet)

		// The dot is after the last element => if the lookahead is in sigma, we reduce
		val toReduce = itemSet.filter { it.dotBefore == it.nodeDef.elements.size }
		// The shift items
		val toShift = itemSet
			.filter { it.dotBefore < it.nodeDef.elements.size }
			.groupBy { it.nodeDef.elements[it.dotBefore] }

		// We do the reduction items first so conflicts are overwritten by the shift items (=> shift by default)
		// TODO: a better way to do that?
		for (item in toReduce) {
			val action = Action.Reduce(
				item.nodeDef.elements.size,
				item.nodeDef.reduction
			)

			for (possibleLookahead in item.sigma) {
				transitions[thisState, possibleLookahead] = action
			}
		}

		for ((char, item) in toShift) {
			// Construct the new item set by shifting the dots to the right
			val newItems = item.map { LR1Item(it.nodeDef, it.dotBefore + 1, it.sigma) }.toMutableSet()

			// Add a transition from this state to the state represented by the items
			transitions[thisState, char] = Action.Shift(getStateOrCreate(newItems))
		}
	}

	/** Performs an epsilon closure on the item set. It modifies the `items` in place. */
	private fun epsilonClosure(items: MutableSet<LR1Item<N>>) {
		val unexpanded = ArrayDeque<LR1Item<N>>()
		unexpanded.addAll(items)

		while (unexpanded.isNotEmpty()) {
			val itemBeingExpanded = unexpanded.pop()
			val nodesToExpand = nodeDefs[itemBeingExpanded.elementAfterDot()] ?: continue
			val sigma = computeSigma(itemBeingExpanded)
			for (node in nodesToExpand) {
				val item = LR1Item(
					node,
					0,
					sigma,
				)

				if (item !in items) {
					items.add(item)
					unexpanded.add(item)
				}
			}
		}
	}

	@Suppress("NestedBlockDepth") // Performance is more important than readability here
	private fun computeSigma(itemBeingExpanded: LR1Item<N>): Set<NodeID> {
		if (itemBeingExpanded.dotBefore + 1 == itemBeingExpanded.nodeDef.elements.size) {
			return itemBeingExpanded.sigma
		}

		val sigma = mutableSetOf<NodeID>()
		val unexpanded = ArrayDeque<NodeID>()

		unexpanded.add(itemBeingExpanded.nodeDef.elements[itemBeingExpanded.dotBefore + 1])

		while (unexpanded.isNotEmpty()) {
			val node = unexpanded.pop()
			if (node !in sigma) {
				sigma += node
				for (definition in nodeDefs[node] ?: emptySet()) {
					// Add the first node of the definition, and if it is nullable, add the second one, and so on
					var i = 0
					do {
						unexpanded += definition.elements[i]
					} while (i < definition.elements.size && isNullable(definition.elements[i++]))

					if (i == definition.elements.size) // The whole sequence is nullable
						sigma.addAll(itemBeingExpanded.sigma)
				}
			}
		}

		return sigma
	}

	/**
	 * Checks if a state represented by the items already exists. If it
	 * doesn't, it gets constructed.
	 * @return The state represented by the items
	 */
	private fun getStateOrCreate(itemSet: MutableSet<LR1Item<N>>) =
		constructorStates[itemSet] ?: stateFactory.new(isErrorRecovering(itemSet)).also { newState ->
			constructorStates[itemSet] = newState
			constructStates(itemSet, newState)
		}

	/**
	 * Checks if the state represented by the `itemSet` should be
	 * error-recovering = if there is an error-recovering node after the dot
	 * in any of the items. E.g.
	 * 	N -> a.Eb, where E is defined as an error-recovering node.
	 */
	private fun isErrorRecovering(itemSet: Set<LR1Item<N>>) =
		itemSet.any { it.elementAfterDot() in errorRecoveringNodes }

	/** Checks if a node is nullable (if it can resolve to epsilon) */
	private fun isNullable(node: NodeID) = nodeDefs[node]?.any { it.elements.isEmpty() } ?: false
}
