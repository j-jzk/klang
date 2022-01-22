package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode
import java.util.ArrayDeque

// placeholder
enum class NodeID {
	INT,
	PLUS,
	TIMES,
	EOF,

	// Nonterminals
	EXPR,
	TOP,
}

data class NodeDef(
	val elements: List<NodeID>,
	val reduction: (List<ASTNode>) -> ASTNode,
)

data class LR1Item(
	val nodeDef: NodeDef,
	val dotBefore: Int, // which element of the def is the dot before
	val sigma: Set<NodeID>,
	val createdAt: State, // which state to go to after reducing
)

object StateFactory {
	private var i = 0
	fun new() = State(i++)
}

// TODO: precompute & memoize everything we can
class ParserDFABuilder(
	val nodeDefs: Map<NodeID, Set<NodeDef>>,
	val topNode: NodeID, // the node needs to have a single definition, NOT ending in EOF
) {
	private val transitions = mutableMapOf<Pair<State, NodeID>, Action>()

	fun go() {
		val topNodeDef = nodeDefs[topNode]!!.first()
		val startState = StateFactory.new()
		var startingSet = setOf(
			LR1Item(topNodeDef, 0, setOf(NodeID.EOF), startState)
		)
		// startingSet = epsilonClosure(startingSet)

		constructStates(startingSet, startState)
	}

	// returns the last state created
	private fun constructStates(itemSet: Set<LR1Item>, thisState: State) {
		val actual = epsilonClosure(itemSet, thisState)
		// the dot is after the last element; if the lookahead is in sigma, we reduce
		val toReduce = actual.filter { it.dotBefore == it.nodeDef.elements.size }
		// shift
		val toShift = actual.filter { it.dotBefore < it.nodeDef.elements.size }.groupBy { it.nodeDef.elements[it.dotBefore] }

		// we do the reduction items first so conflicts are overwritten by the shift items (=> shift by default)
		// TODO: a better way to do that?
		for (item in toReduce) {
			val action = Action.Reduce(
				item.nodeDef.elements.size,
				item.nodeDef.reduction,
				item.createdAt
			)

			for (possibleLookahead in item.sigma) {
				transitions[thisState to possibleLookahead] = action
			}
		}

		for ((char, item) in toShift) {
			// construct the action (transition)
			val action = Action.Shift(StateFactory.new())
			// construct the new item set by shifting the dots to the right
			val newItems = item.map { LR1Item(it.nodeDef, it.dotBefore + 1, it.sigma, it.createdAt) }.toSet()
			// construct the states going out of the newly created state
			constructStates(newItems, action.nextState)
			transitions[thisState to char] = action
		}
	}

	private fun epsilonClosure(items: Set<LR1Item>, currentState: State): Set<LR1Item> {
		val result = mutableSetOf<LR1Item>()
		result.addAll(items)

		val unexpanded = ArrayDeque<LR1Item>()
		unexpanded.addAll(items)

		while (unexpanded.isNotEmpty()) {
			val itemBeingExpanded = unexpanded.pop()
			val nodesToExpand = nodeDefs[itemBeingExpanded.nodeDef.elements.getOrNull(itemBeingExpanded.dotBefore)]
			val sigma = computeSigma(itemBeingExpanded)
			for (node in nodesToExpand ?: emptySet()) {
				val item = LR1Item(
					node,
					0,
					sigma,
					currentState
				)

				if (item !in result) {
					result.add(item)
					unexpanded.add(item)
				}
			}
		}

		return result
	}

	private fun computeSigma(itemBeingExpanded: LR1Item): Set<NodeID> {
		// we won't actually get such an input, because such items aren't being expanded
		// if (itemBeingExpanded.dotBefore == itemBeingExpanded.nodeDef.elements.size) {
		// 	return itemBeingExpanded.sigma
		// }
		val sigma = mutableSetOf<NodeID>()
		val unexpanded = ArrayDeque<NodeID>()
			
		// if (isNullable(itemBeingExpanded.nodeDef.elements.subList(itemBeingExpanded.dotBefore, itemBeingExpanded.nodeDef.elements.size - 1))) {
		// 	sigma.addAll(itemBeingExpanded.sigma)
		// }

		unexpanded.add(itemBeingExpanded.nodeDef.elements[itemBeingExpanded.dotBefore])
		
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

	/** Checks if a node is nullable (if it can resolve to epsilon) */
	private fun isNullable(node: NodeID) = nodeDefs[node]?.any { it.elements.isEmpty() } ?: false
}