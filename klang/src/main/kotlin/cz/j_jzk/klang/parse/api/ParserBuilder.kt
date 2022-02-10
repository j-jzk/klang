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
		val actualReduction: (List<ASTNode<D>>) -> ASTNode<D> = { nodeList ->
			ASTNode.Data(
				actualID,
				definition.reduction(nodeList.map { node ->
					(node as ASTNode.Data<D>).data // STOPSHIP: this cast WILL FAIL if there is an errorneous node!!!
				})
			)
		}
		nodeDefs[actualID]!!.add(NodeDef(actualDefinition, actualReduction))
	}

	fun def(vararg definition: I, reduction: (List<D>) -> D) = IntermediateNodeDefinition(definition.toList(), reduction)

	var topNode: I? = null

	fun build(): DFA<D> {
		requireNotNull(topNode) { "The top node of the grammar must be set" }
		return DFABuilder(nodeDefs, NodeID.ID(topNode), emptyList()).build()
	}

	data class IntermediateNodeDefinition<I, D>(val definition: List<I>, val reduction: (List<D>) -> D)
}
