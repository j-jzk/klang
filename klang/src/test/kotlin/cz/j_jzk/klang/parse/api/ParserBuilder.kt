package cz.j_jzk.klang.parse.api

import org.apache.commons.collections4.map.LazyMap
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.ParserWrapper
import cz.j_jzk.klang.parse.UnexpectedTokenError
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
fun parser(init: ParserBuilder.() -> Unit) = ParserBuilder().also { it.init() }

data class AnyNodeID(val v: Any): NodeID<Any>()

/**
 * An interface for building a parser. You probably don't want to use this
 * directly, but instead use the function parser() from this package.
 */
class ParserBuilder {
	private val actualNodeDefs = mutableMapOf<NodeID<*>, MutableSet<NodeDef>>()
	private val nodeDefs = LazyMap.lazyMap<NodeID<*>, MutableSet<NodeDef>>(actualNodeDefs) { -> mutableSetOf() }
	private val errorRecoveringNodes = mutableSetOf<NodeID<*>>()
	private var conversionsMap: Map<Any, (String) -> Any>? = null
	private var errorCallback: ((UnexpectedTokenError) -> Unit)? = null

	/** Maps a node to its definition. */
	infix fun Any.to(definition: IntermediateNodeDefinition) {
		val actualReduction = wrapReduction(AnyNodeID(this), definition.reduction)
		nodeDefs[this]!!.add(NodeDef(definition.definition, actualReduction))
	}

	/** Creates a node definition */
	fun def(vararg definition: Any, reduction: (List<Any?>) -> Any) =
		IntermediateNodeDefinition(definition.map { AnyNodeID(it) }, reduction)

	/** The top node of the grammar (the root of the AST) */
	var topNode: NodeID<*>? = null

	/**
	 * Marks `nodes` to be error-recovering. These nodes will be then used to
	 * contain syntax errors.
	 */
	fun errorRecovering(vararg nodes: NodeID<*>) {
		errorRecoveringNodes.addAll(nodes)
	}

	/**
	 * Declare a function to be called when a syntax error is encountered.
	 * Error recovery is handled automatically. The erroneous token is passed
	 * into the function for error reporting.
	 */
	fun onError(callback: (UnexpectedTokenError) -> Unit) {
		require(errorCallback == null) { "Only one `onError` block is allowed" }
		errorCallback = callback
	}

	/** Declare conversions from lexer tokens (strings) to AST node values */
	fun conversions(init: ConverterBuilder.() -> Unit) {
		require(conversionsMap == null) { "Only one `conversions` block is allowed" }
		val builder = ConverterBuilder()
		builder.init()
		conversionsMap = builder.getConversions()
	}

	/** Builds and returns the parser */
	fun getParser(): ParserWrapper {
		val topNodeNotNull = topNode;
		requireNotNull(topNodeNotNull) { "The top node of the grammar must be set" }
		val nullSafeConversions = requireNotNull(conversionsMap) {
			"A `conversions` block must be present to define the conversions between the tokens and node values"
		}

		// Add the top node to the error-recovering nodes so the parser doesn't
		// fail completely if the user hasn't specified any error-recovering nodes
		errorRecoveringNodes += topNodeNotNull

		val dfa = DFABuilder(actualNodeDefs, topNodeNotNull, errorRecoveringNodes.toList(), errorCallback ?: { })
			.build()
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
	private fun wrapReduction(nodeID: NodeID<*>, reduction: (List<Any?>) -> Any): (List<ASTNode>) -> ASTNode =
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
	data class IntermediateNodeDefinition(val definition: List<NodeID<*>>, val reduction: (List<Any?>) -> Any)
}
