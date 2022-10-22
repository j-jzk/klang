package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.util.PositionInfo
import cz.j_jzk.klang.util.mergeSetValues
import org.apache.commons.collections4.map.LazyMap

fun sele(init: SeleBuilder.() -> Unit): SeleBuilder =
        SeleBuilder().also { it.init() }

class SeleBuilder {
    private val lexerDef = LexerDefinition()
    private val parserDef = ParserDefinition()

    /** Creates a node definition */
	fun def(vararg definition: NodeID, reduction: (List<Any?>) -> Any) =
        IntermediateNodeDefinition(definition.toList(), reduction)

    /** Maps a node to its definition. */
	infix fun NodeID.to(definition: IntermediateNodeDefinition) {
		val actualReduction = wrapReduction(this, definition.reduction)
		parserDef.nodeDefs[this]!!.add(NodeDef(definition.definition, actualReduction))
	}

    /**
	 * Marks `nodes` to be error-recovering. These nodes will be then used to
	 * contain syntax errors.
	 */
	fun errorRecovering(vararg nodes: NodeID) {
		parserDef.errorRecoveringNodes.addAll(nodes)
	}
    
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
    val ignored: MutableList<NFA> = mutableListOf()
    var unexpectedCharacterHandler: ((Char, PositionInfo) -> Unit)? = null
    fun getLexer() = LexerWrapper(Lexer(tokenDefs, ignored), unexpectedCharacterHandler ?: { _, _ -> })

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

    fun include(other: ParserDefinition) {
        nodeDefs.mergeSetValues(other.nodeDefs)
        errorRecoveringNodes.addAll(other.errorRecoveringNodes)
    }
}
