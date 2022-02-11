package cz.j_jzk.klang.parse.api

import org.apache.commons.collections4.map.LazyMap
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.DFABuilder

fun <I, D> parser(init: ParserBuilder<I, D>.() -> Unit) = ParserBuilder<I, D>().also { it.init() }

class ParserBuilder<I, D> {
	private val nodeDefs = LazyMap.lazyMap<NodeID, MutableSet<NodeDef<D>>>(mutableMapOf()) { -> mutableSetOf() }

	infix fun I.to(definition: IntermediateNodeDefinition<I, D>) {
		// Maybe we should move this logic to a separate wrapper class that does this bc this is just a total mess
		val actualID = NodeID.ID(this)
		val actualDefinition = definition.definition.map { NodeID.ID(it) }
		val actualReduction = wrapReduction(actualID, definition.reduction)
		nodeDefs[actualID]!!.add(NodeDef(actualDefinition, actualReduction))
	}

	fun def(vararg definition: I, reduction: (List<D>) -> D) = IntermediateNodeDefinition(definition.toList(), reduction)

	var topNode: I? = null

	fun build(): DFA<D> {
		requireNotNull(topNode) { "The top node of the grammar must be set" }
		return DFABuilder(nodeDefs, NodeID.ID(topNode), emptyList()).build()
	}

	/**
	 * Returns an altered reduction function which:
	 *   - translates the parameters and return values to/from `ASTNode`
	 *   - if any of the nodes to be reduced is `Erroneous`, it also returns
	 *     an Erroneous node, because the reduction couldn't work with it
	 *     (it has no value) and a correct program couldn't be created from
	 *     such an AST anyway
	 */
	private fun wrapReduction(nodeID: NodeID, reduction: (List<D>) -> D): (List<ASTNode<D>>) -> ASTNode<D> =
		{ nodeList ->
			if (nodeList.none { it is ASTNode.Erroneous })
				ASTNode.Data(
						nodeID,
						reduction(nodeList.map { node ->
							(node as ASTNode.Data<D>).data
						})
				)
			else
				ASTNode.Erroneous(nodeID)
		}

	data class IntermediateNodeDefinition<I, D>(val definition: List<I>, val reduction: (List<D>) -> D)
}
