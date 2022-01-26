package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import java.util.ArrayDeque

internal data class LR1Item(
	val nodeDef: NodeDef,
	val dotBefore: Int, // which element of the def is the dot before
	val sigma: Set<NodeID>,
	val createdAt: State, // which state to go to after reducing
) {
	/* This is a hack to exclude the createdAt attribute from being compared,
	 * so that the constructorStates table works as expected.
	 * TODO: find a better way to do this (in the algorithm itself) */
	override fun equals(other: Any?) =
		other is LR1Item
		&& other.nodeDef == this.nodeDef
		&& other.dotBefore == this.dotBefore
		&& other.sigma == this.sigma
	override fun hashCode() = nodeDef.hashCode() xor dotBefore xor sigma.hashCode()
}

/**
 * This class builds a parser from the formal grammar.
 */
// TODO: precompute & memoize everything we can
class DFABuilder(
	/** The formal grammar */
	val nodeDefs: Map<NodeID, Set<NodeDef>>,

	/**
	 * The top node of the grammar. It needs to have a single definition, not
	 * ending in EOF.
	 */
	val topNode: NodeID,
) {
	private val transitions = mutableMapOf<Pair<State, NodeID>, Action>()

	/**
	 * This variable maps the states as seen by the builder to the states seen
	 * by the DFA
	 */
	private val constructorStates = mutableMapOf<Set<LR1Item>, State>()

	/** This function constructs the parser and returns it. */
	fun build(): DFA {
		val topNodeDef = nodeDefs[topNode]!!.first()
		val startState = StateFactory.new()
		var startingSet = mutableSetOf(
			LR1Item(topNodeDef, 0, setOf(NodeID.EOF), startState)
		)

		constructorStates[startingSet] = startState
		constructStates(startingSet, startState)

		return DFA(transitions, topNode, startState)
	}

	private fun constructStates(itemSet: MutableSet<LR1Item>, thisState: State) {
		epsilonClosure(itemSet, thisState)

		// The dot is after the last element => if the lookahead is in sigma, we reduce
		val toReduce = itemSet.filter { it.dotBefore == it.nodeDef.elements.size }
		// The shift items
		val toShift = itemSet.filter { it.dotBefore < it.nodeDef.elements.size }.groupBy { it.nodeDef.elements[it.dotBefore] }

		// We do the reduction items first so conflicts are overwritten by the shift items (=> shift by default)
		// TODO: a better way to do that?
		for (item in toReduce) {
			val action = Action.Reduce(
				item.nodeDef.elements.size, // TODO: rethink what is passed to the reduction
				item.nodeDef.reduction,
				item.createdAt
			)

			for (possibleLookahead in item.sigma) {
				transitions[thisState to possibleLookahead] = action
			}
		}

		for ((char, item) in toShift) {
			// Construct the new item set by shifting the dots to the right
			val newItems = item.map { LR1Item(it.nodeDef, it.dotBefore + 1, it.sigma, it.createdAt) }.toMutableSet()

			// Add a transition from this state to the state represented by the items
			transitions[thisState to char] = Action.Shift(getStateOrCreate(newItems))
		}
	}

	/** Performs an epsilon closure on the item set. It modifies the `items` in place. */
	private fun epsilonClosure(items: MutableSet<LR1Item>, currentState: State) {
		val unexpanded = ArrayDeque<LR1Item>()
		unexpanded.addAll(items)

		while (unexpanded.isNotEmpty()) {
			val itemBeingExpanded = unexpanded.pop()
			val nodesToExpand = nodeDefs[itemBeingExpanded.nodeDef.elements.getOrNull(itemBeingExpanded.dotBefore)] ?: continue
			val sigma = computeSigma(itemBeingExpanded)
			for (node in nodesToExpand) {
				val item = LR1Item(
					node,
					0,
					sigma,
					currentState
				)

				if (item !in items) {
					items.add(item)
					unexpanded.add(item)
				}
			}
		}
	}

	@Suppress("NestedBlockDepth") // Performance is more important than readability here
	private fun computeSigma(itemBeingExpanded: LR1Item): Set<NodeID> {
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
	private fun getStateOrCreate(itemSet: MutableSet<LR1Item>): State {
		val state: State
		if (constructorStates[itemSet] == null) {
			state = StateFactory.new()
			constructorStates[itemSet] = state
			constructStates(itemSet, state)
		} else {
			state = constructorStates[itemSet]!!
		}

		return state
	}

	/** Checks if a node is nullable (if it can resolve to epsilon) */
	private fun isNullable(node: NodeID) = nodeDefs[node]?.any { it.elements.isEmpty() } ?: false
}
