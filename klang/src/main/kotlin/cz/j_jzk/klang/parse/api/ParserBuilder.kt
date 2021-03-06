package cz.j_jzk.klang.parse.api

import org.apache.commons.collections4.map.LazyMap
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.ParserWrapper
import cz.j_jzk.klang.parse.algo.DFABuilder
import cz.j_jzk.klang.util.PositionInfo

/**
 * A function to create a parser.
 *
 * Type parameters:
 *   I - A type for identifying nodes of the AST. Enums are the most suitable
 *       for this, but you may as well use anything you want
 *   D - The type of the data stored in the AST
 */
fun <I, D> parser(init: ParserBuilder<I, D>.() -> Unit) = ParserBuilder<I, D>().also { it.init() }

/**
 * An interface for building a parser. You probably don't want to use this
 * directly, but instead use the function parser() from this package.
 */
class ParserBuilder<I, D> {
	private val actualNodeDefs = mutableMapOf<NodeID, MutableSet<NodeDef<D>>>()
	private val nodeDefs = LazyMap.lazyMap<NodeID, MutableSet<NodeDef<D>>>(actualNodeDefs) { -> mutableSetOf() }
	private val errorRecoveringNodes = mutableSetOf<NodeID>()
	private var conversionsMap: Map<I, (String) -> D>? = null
	private var errorCallback: ((ASTNode<D>) -> Unit)? = null

	/** Maps a node to its definition. */
	infix fun I.to(definition: IntermediateNodeDefinition<I, D>) {
		val actualID = NodeID.ID(this)
		val actualDefinition = definition.definition.map { NodeID.ID(it) }
		val actualReduction = wrapReduction(actualID, definition.reduction)
		nodeDefs[actualID]!!.add(NodeDef(actualDefinition, actualReduction))
	}

	/** Creates a node definition */
	fun def(vararg definition: I, reduction: (List<D?>) -> D) = IntermediateNodeDefinition(definition.toList(), reduction)

	/** The top node of the grammar (the root of the AST) */
	var topNode: I? = null

	/**
	 * Marks `nodes` to be error-recovering. These nodes will be then used to
	 * contain syntax errors.
	 */
	fun errorRecovering(vararg nodes: I) {
		errorRecoveringNodes += nodes.map { NodeID.ID(it) }
	}

	/**
	 * Declare a function to be called when a syntax error is encountered.
	 * Error recovery is handled automatically. The erroneous token is passed
	 * into the function for error reporting.
	 */
	fun onError(callback: (ASTNode<D>) -> Unit) {
		require(errorCallback == null) { "Only one `onError` block is allowed" }
		errorCallback = callback
	}

	/** Declare conversions from lexer tokens (strings) to AST node values */
	fun conversions(init: ConverterBuilder<I, D>.() -> Unit) {
		require(conversionsMap == null) { "Only one `conversions` block is allowed" }
		val builder = ConverterBuilder<I, D>()
		builder.init()
		conversionsMap = builder.getConversions()
	}

	/** Builds and returns the parser */
	fun getParser(): ParserWrapper<I, D> {
		requireNotNull(topNode) { "The top node of the grammar must be set" }
		val nullSafeConversions = requireNotNull(conversionsMap) {
			"A `conversions` block must be present to define the conversions between the tokens and node values"
		}

		// Add the top node to the error-recovering nodes so the parser doesn't
		// fail completely if the user hasn't specified any error-recovering nodes
		errorRecoveringNodes += NodeID.ID(topNode)

		val dfa = DFABuilder(actualNodeDefs, NodeID.ID(topNode), errorRecoveringNodes.toList(), errorCallback ?: { }).build()
		return ParserWrapper(dfa, nullSafeConversions)
	}

	/**
	 * Returns an altered reduction function which:
	 *   - translates the parameters and return values to/from `ASTNode`
	 *   - if any of the nodes to be reduced is `Erroneous`, it also returns
	 *     an Erroneous node, because the reduction couldn't work with it
	 *     (it has no value) and a correct program couldn't be created from
	 *     such an AST anyway
	 */
	private fun wrapReduction(nodeID: NodeID, reduction: (List<D?>) -> D): (List<ASTNode<D>>) -> ASTNode<D> =
		{ nodeList ->
			if (nodeList.none { it is ASTNode.Erroneous })
				ASTNode.Data(
						nodeID,
						reduction(nodeList.map { node ->
							when (node) {
								is ASTNode.Data -> node.data
								is ASTNode.NoValue -> null
								else -> throw IllegalStateException("This should never happen")
							}
						}),

						nodeList.firstOrNull { it.position.character != -1 }
								?.position ?: PositionInfo("__undefined", -1)
				)
			else
				ASTNode.Erroneous(nodeID, nodeList.first().position)
		}

	/**
	 * A structure used internally to represent a node definition. (This is
	 * used to allow for a syntax with fewer braces.)
	 */
	data class IntermediateNodeDefinition<I, D>(val definition: List<I>, val reduction: (List<D?>) -> D)
}
