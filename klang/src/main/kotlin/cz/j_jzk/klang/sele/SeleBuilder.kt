package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.parse.algo.DFA
import cz.j_jzk.klang.parse.algo.DFABuilder
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.util.mergeSetValues
import org.apache.commons.collections4.map.LazyMap
import cz.j_jzk.klang.lex.re.compileRegex

/**
 * A function for creating a sele.
 */
// TODO: add an example to the documentation
fun sele(init: SeleBuilder.() -> Unit): SeleBuilder =
		SeleBuilder().also { it.init() }

/**
 * An interface for defining a sele. You most probably don't want to create
 * this class directly, but instead use the `sele()` function from this package.
 */
class SeleBuilder {
	private val lexerDef = LexerDefinition()
	private val parserDef = ParserDefinition()

	/** Creates a node definition */
	fun def(vararg definition: NodeID, reduction: (List<Any?>) -> Any) =
		IntermediateNodeDefinition(definition.toList(), reduction)

	/** Maps a node to its definition. */
	infix fun NodeID.to(definition: IntermediateNodeDefinition) {
		val actualReduction = wrapReduction(this, definition.reduction)
		// TODO: test that lexer ignores defined after the node definition get added too
		parserDef.nodeDefs[this]!!.add(NodeDef(definition.definition, actualReduction, parserDef.lexerIgnores))
	}

	/**
	 * Marks `nodes` to be error-recovering. These nodes will be then used to
	 * contain syntax errors.
	 */
	fun errorRecovering(vararg nodes: NodeID) {
		parserDef.errorRecoveringNodes.addAll(nodes)
	}

	/** Sets the root node of this sele */
	fun setTopNode(node: NodeID) {
		parserDef.topNode = node
	}

	/** Defines a regex node */
	fun re(regex: String): NodeID =
		RegexNodeID(regex).also { lexerDef.tokenDefs[compileRegex(regex).fa] = it }

	/** Ignore the specified regexes when reading the input */
	fun ignoreRegexes(vararg regexes: String) {
		parserDef.lexerIgnores.addAll(regexes.map { compileRegex(it).fa })
	}

	/**
	 * Finalizes the definition and returns the sele, which can be used to
	 * do the parsing.
	 */
	fun getSele(): Sele = Sele(lexerDef.getLexer(), parserDef.getParser())

	/**
	 * Returns an altered reduction function which:
	 *   - translates the parameters and return values to/from `ASTNode`
	 *   - if any of the nodes to be reduced is `Erroneous`, it also returns
	 *     an Erroneous node, because the reduction couldn't work with it
	 *     (it has no value) and a correct program couldn't be created from
	 *     such an AST anyway
	 */
	private fun wrapReduction(nodeID: NodeID, reduction: (List<Any?>) -> Any): (List<ASTNode>) -> ASTNode =
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
	data class IntermediateNodeDefinition(val definition: List<NodeID>, val reduction: (List<Any?>) -> Any)
}

internal class LexerDefinition {
	val tokenDefs: LinkedHashMap<NFA, NodeID> = linkedMapOf()

	var unexpectedCharacterHandler: ((Char, PositionInfo) -> Unit)? = null
	fun getLexer() = LexerWrapper(Lexer(tokenDefs, /* ignored */), unexpectedCharacterHandler ?: { _, _ -> })

	fun include(other: LexerDefinition) {
		tokenDefs.putAll(other.tokenDefs)
		// TODO: include ignored regexes
	}
}

internal class ParserDefinition {
	/** The actual node definition data */
	val actualNodeDefs: MutableMap<NodeID, MutableSet<NodeDef>> = mutableMapOf()
	/** Used for simpler code - returns an empty set when a node ID isn't defined */
	val nodeDefs: LazyMap<NodeID, MutableSet<NodeDef>> = LazyMap.lazyMap(actualNodeDefs) { -> mutableSetOf() }
	val errorRecoveringNodes: MutableSet<NodeID> = mutableSetOf()
	var errorCallback: ((UnexpectedTokenError) -> Unit)? = null
	/** The top node of the grammar (the root of the AST) */
	var topNode: NodeID? = null
	/** Lexer ignores in the current context */
	val lexerIgnores = mutableSetOf<NFA>()

	/** Builds and returns the parser */
	fun getParser(): DFA {
		val topNodeNotNull = topNode;
		requireNotNull(topNodeNotNull) { "The top node of the grammar must be set" }

		// Add the top node to the error-recovering nodes so the parser doesn't
		// fail completely if the user hasn't specified any error-recovering nodes
		errorRecoveringNodes += topNodeNotNull

		return DFABuilder(actualNodeDefs, topNodeNotNull, errorRecoveringNodes.toList(), errorCallback ?: { })
			.build()
	}

	fun include(other: ParserDefinition) {
		nodeDefs.mergeSetValues(other.nodeDefs)
		errorRecoveringNodes.addAll(other.errorRecoveringNodes)
	}
}
