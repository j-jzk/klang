package cz.j_jzk.klang.parse.algo

import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.lex.re.CompiledRegex
import java.util.ArrayDeque

internal data class LR1Item(
	val nodeDef: NodeDef,
	val dotBefore: Int, // which element of the def is the dot before
	val sigma: Set<NodeID<*>>,
	/**
	 * The regexes to be ignored when the end of the item is reached.
	 *
	 * The reason for this is that when we reach the end of the item, the next
	 * node, which correctly appears in the sigma set, might be behind an
	 * ignored character.
	 *
	 * Thus, when we reach the end of this definition, we also need to ignore
	 * the regexes ignored in the lesana we reduce into.
	 */
	val ignoreAfter: Set<CompiledRegex>,
)

/**
 * A convenience function for getting the element of the item after the dot.
 * If the dot is at the end, this returns null.
 */
private fun LR1Item.elementAfterDot(): NodeID<*>? =
	nodeDef.elements.getOrNull(dotBefore)

/**
 * This class builds a parser from the formal grammar.
 */
// TODO: precompute & memoize everything we can
class DFABuilder(
	/** The formal grammar */
	val nodeDefs: Map<NodeID<Any?>, Set<NodeDef>>,

	/**
	 * The top node of the grammar. It needs to have a single definition, not
	 * ending in EOF.
	 */
	val topNode: NodeID<*>,

	/**
	 * The error-recovering nodes (nodes which will be used to contain syntax
	 * errors)
	 */
	val errorRecoveringNodes: List<NodeID<*>>,

	/**
	 * A callback used when the parser encounters a syntax error.
	 */
	val onUnexpectedToken: (UnexpectedTokenError) -> Unit,
) {
	private val transitions = HashBasedTable.create<State, NodeID<*>, Action>()

	/**
	 * This variable maps the states as seen by the builder to the states seen
	 * by the DFA
	 */
	private val constructorStates = mutableMapOf<Set<LR1Item>, State>()

	private val stateFactory = StateFactory()

	/** Assigns states to the lexer ignores that should apply in them */
	private val lexerIgnores = mutableMapOf<State, Set<CompiledRegex>>()

	internal companion object {
		/*
		 * This is here so we can unit test (functions can't be structurally
		 * compared, so we must compare the exact same function)
		 */
		val identityReduction: (List<ASTNode>) -> ASTNode = { it[0] }
	}

	/** This function constructs the parser and returns it. */
	fun build(): DFA {
		val topNodeDef = nodeDefs[topNode]!!.first()
		var startingSet = mutableSetOf(
			LR1Item(topNodeDef, 0, setOf(EOFNodeID), emptySet())
		)
		// The top state will always be error-recovering (for protection)
		val startState = stateFactory.new(true)

		constructorStates[startingSet] = startState
		constructStates(startingSet, startState)

		// Final state (needed for e-r to work properly)
		val finalState = stateFactory.new(false)
		transitions[startState, topNode] = Action.Shift(finalState)
		transitions[finalState, EOFNodeID] = Action.Reduce(1, identityReduction)
		// the ignore set for the final state doesn't get passed to the lexer,
		// but we need a value anyway to not run into a NPE
		lexerIgnores[finalState] = emptySet()

		return DFA(transitions, topNode, startState, errorRecoveringNodes, onUnexpectedToken, lexerIgnores)
	}

	private fun constructStates(itemSet: MutableSet<LR1Item>, thisState: State) {
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
			val newItems = item.map { LR1Item(it.nodeDef, it.dotBefore + 1, it.sigma, it.ignoreAfter) }.toMutableSet()

			// Add a transition from this state to the state represented by the items
			transitions[thisState, char] = Action.Shift(getStateOrCreate(newItems))
		}

		// Assign lexer ignores to the state according to the ignores specified in the NodeDefs
		lexerIgnores[thisState] = computeStateIota(itemSet)
	}

	/** Performs an epsilon closure on the item set. It modifies the `items` in place. */
	private fun epsilonClosure(items: MutableSet<LR1Item>) {
		val unexpanded = ArrayDeque<LR1Item>()
		unexpanded.addAll(items)

		while (unexpanded.isNotEmpty()) {
			val itemBeingExpanded = unexpanded.pop()
			val nodesToExpand = nodeDefs[itemBeingExpanded.elementAfterDot()] ?: continue
			val (sigma, iota) = computeSigmaIota(itemBeingExpanded)
			for (node in nodesToExpand) {
				val item = LR1Item(
					node,
					0,
					sigma,
					iota,
				)

				if (item !in items) {
					items.add(item)
					unexpanded.add(item)
				}
			}
		}
	}

	@Suppress("CognitiveComplexMethod", "NestedBlockDepth") // Performance is more important than readability here
	private fun computeSigmaIota(itemBeingExpanded: LR1Item): Pair<Set<NodeID<*>>, Set<CompiledRegex>> {
		if (itemBeingExpanded.dotBefore + 1 == itemBeingExpanded.nodeDef.elements.size) {
			return Pair(itemBeingExpanded.sigma, itemBeingExpanded.ignoreAfter)
		}

		val sigma = mutableSetOf<NodeID<*>>()
		val unexpanded = ArrayDeque<NodeID<*>>()
		val iota = mutableSetOf<CompiledRegex>()
		iota.addAll(itemBeingExpanded.nodeDef.lexerIgnores)

		unexpanded.add(itemBeingExpanded.nodeDef.elements[itemBeingExpanded.dotBefore + 1])

		while (unexpanded.isNotEmpty()) {
			val node = unexpanded.pop()
			if (node !in sigma) {
				sigma += node
				for (definition in nodeDefs[node] ?: emptySet()) {
					// Add the first node of the definition, and if it is nullable, add the second one, and so on
					var i = 0
					if (definition.elements.isNotEmpty())
						do {
							unexpanded += definition.elements[i]
						} while (i < definition.elements.size && isNullable(definition.elements[i++]))

					if (i == definition.elements.size) // The whole sequence is nullable
						sigma.addAll(itemBeingExpanded.sigma)

					iota.addAll(definition.lexerIgnores)
				}
			}
		}

		return Pair(sigma, iota)
	}

	/** Computes the set of lexer ignores for a state (set of LR(1) items) */
	private fun computeStateIota(itemSet: Set<LR1Item>): Set<CompiledRegex> =
		itemSet
			.map {
				it.nodeDef.lexerIgnores +
					if (it.dotBefore == it.nodeDef.elements.size)
						it.ignoreAfter
					else
						emptySet()
			}
			.reduce { item, acc -> acc + item }

	/**
	 * Checks if a state represented by the items already exists. If it
	 * doesn't, it gets constructed.
	 * @return The state represented by the items
	 */
	private fun getStateOrCreate(itemSet: MutableSet<LR1Item>) =
		constructorStates[itemSet] ?: stateFactory.new(isErrorRecovering(itemSet)).also { newState ->
			constructorStates[itemSet] = newState

			// We must duplicate the item set because it is modified by constructStates
			var newItemSet = mutableSetOf<LR1Item>()
			newItemSet.addAll(itemSet)

			constructStates(newItemSet, newState)
		}

	/**
	 * Checks if the state represented by the `itemSet` should be
	 * error-recovering = if there is an error-recovering node after the dot
	 * in any of the items. E.g.
	 * 	N -> a.Eb, where E is defined as an error-recovering node.
	 */
	private fun isErrorRecovering(itemSet: Set<LR1Item>) =
		itemSet.any { it.elementAfterDot() in errorRecoveringNodes }

	/** Checks if a node is nullable (if it can resolve to epsilon) */
	private fun isNullable(node: NodeID<*>) = nodeDefs[node]?.any { it.elements.isEmpty() } ?: false
}
