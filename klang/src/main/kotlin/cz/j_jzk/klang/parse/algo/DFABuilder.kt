package cz.j_jzk.klang.parse.algo

import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.lex.re.CompiledRegex
import org.apache.commons.collections4.map.LazyMap
import java.util.ArrayDeque
import kotlin.collections.mutableSetOf

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

    /** The NULLABLE set from literature, that is, which NodeIDs can resolve to the empty word (epsilon) */
    private val nullableNodeIds: Set<NodeID<*>>
    /** The FIRST set from literature. FIRST(X) contains all the terminals that X can begin with. */
    private val firstTerminals: Map<NodeID<*>, Set<NodeID<*>>>
    init {
        /// initialize FIRST and NULLABLE
        val nullable = mutableSetOf<NodeID<*>>()
        val first = LazyMap.lazyMap(mutableMapOf<NodeID<*>, MutableSet<NodeID<*>>>()) { _ -> mutableSetOf() }

        val allNodeIds = mutableSetOf<NodeID<*>>().apply {
            addAll(nodeDefs.keys)
            addAll(nodeDefs.values.asSequence().flatten().flatMap { it.elements })
        }

        // for a description of the algorithm, see the Dragon book (1988 ed.), page 189
        var somethingChanged = false
        fun Boolean.registerChange() { somethingChanged = this || somethingChanged }
        do {
            somethingChanged = false

            val nodesToRemove = mutableSetOf<NodeID<*>>()
            for (nodeId in allNodeIds) {
                // 1. if X is terminal, then FIRST(X) = {X}
                if (isTerminal(nodeId)) {
                    first[nodeId]!!.add(nodeId).registerChange()
                    // remove from allNodeIds so we don't needlessly iterate over it again
                    // (we can't remove items from a list while iterating over it)
                    nodesToRemove.add(nodeId)
                    continue
                }

                for (def in nodeDefs[nodeId]!!) {
                    // 2. if X -> ε is a production, then mark X nullable
                    if (def.elements.isEmpty()) {
                        nullable.add(nodeId).registerChange()
                        continue
                    }

                    // 3. (modified) if X -> Y1 Y2 ... Yk is a production:
                    //  - add all of FIRST(Y1) to FIRST(X)
                    //  - if Y1 is nullable, add FIRST(Y2) to FIRST(X) and so on
                    var defIsNullable = true
                    for (defElement in def.elements) {
                        first[nodeId]!!.addAll(first[defElement]!!).registerChange()
                        // traditionally, FIRST contains only terminals, but we need nonterminals for constructing
                        // lexer ignore (iota) sets
                        first[nodeId]!!.add(defElement).registerChange()

                        if (defElement !in nullable) {
                            defIsNullable = false
                            break
                        }
                    }
                    // if the whole definition is nullable, mark X as nullable
                    if (defIsNullable)
                        nullable.add(nodeId).registerChange()
                }
            }

            allNodeIds.removeAll(nodesToRemove)
            nodesToRemove.clear()
        } while (somethingChanged)

        nullableNodeIds = nullable
        firstTerminals = first
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
			val expandedNodeDefs = nodeDefs[itemBeingExpanded.elementAfterDot()] ?: continue
            val sigma = computeSigma(itemBeingExpanded)
            val iota = computeIota(itemBeingExpanded)

			for (nodeDef in expandedNodeDefs) {
				val item = LR1Item(
					nodeDef,
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

    /**
     * Computes the sigma set for an LR1 item, that is, what symbols may appear after it (for lookahead)
     */
	private fun computeSigma(itemBeingExpanded: LR1Item): Set<NodeID<*>> {
//		if (itemBeingExpanded.dotBefore + 1 == itemBeingExpanded.nodeDef.elements.size) {
//			return itemBeingExpanded.sigma
//		}

        val sigma = mutableSetOf<NodeID<*>>()

        var isWholeSequenceNullable = true
        for (i in itemBeingExpanded.dotBefore+1 until itemBeingExpanded.nodeDef.elements.size) {
            val currentSymbol = itemBeingExpanded.nodeDef.elements[i]
            sigma.addAll(firstTerminals[currentSymbol]!!)
            if (currentSymbol !in nullableNodeIds) {
                isWholeSequenceNullable = false
                break
            }
        }

        if (isWholeSequenceNullable) {
            sigma.addAll(itemBeingExpanded.sigma)
        }

        return sigma
	}

    /**
     * Computes the iota set for an LR1 item, that is, which lexer ignores should be applied
     * in this item's state
     */
    // TODO: precompute this?
    private fun computeIota(itemBeingExpanded: LR1Item): Set<CompiledRegex> {
        if (itemBeingExpanded.dotBefore + 1 == itemBeingExpanded.nodeDef.elements.size) {
            return itemBeingExpanded.ignoreAfter
        }

        val symbolAfterNext = itemBeingExpanded.nodeDef.elements[itemBeingExpanded.dotBefore + 1]

        val iota = mutableSetOf<CompiledRegex>()
        iota.addAll(itemBeingExpanded.nodeDef.lexerIgnores)
        iota.addAll(
            firstTerminals[symbolAfterNext]!!
                .map { nodeDefs[it] ?: emptySet() }
                .flatten()
                .flatMap { it.lexerIgnores }
        )

        return iota
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

    private fun isTerminal(node: NodeID<*>) = node !in nodeDefs
}
