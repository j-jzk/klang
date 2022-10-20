package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.lex.re.fa.NFA
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.UnexpectedTokenError
import cz.j_jzk.klang.util.PositionInfo
import org.apache.commons.collections4.map.LazyMap

fun sele(init: SeleBuilder.() -> Unit): SeleBuilder =
        SeleBuilder().also { it.init() }

class SeleBuilder {
    private val lexerDef = LexerDefinition()
    private val parserDef = ParserDefinition()
}

internal data class LexerDefinition(
        val tokenDefs: LinkedHashMap<NFA, Any> = linkedMapOf(),
        val ignored: MutableList<NFA> = mutableListOf(),
        var unexpectedCharacterHandler: ((Char, PositionInfo) -> Unit)? = null
) {
    fun getLexer() = LexerWrapper(Lexer(tokenDefs, ignored), unexpectedCharacterHandler ?: { _, _ -> })
}

internal data class ParserDefinition(
        /** The actual node definition data */
        val actualNodeDefs: MutableMap<NodeID, MutableSet<NodeDef>> = mutableMapOf(),
        /** Used for simpler code - returns an empty set when a node ID isn't defined */
        val nodeDefs: LazyMap<NodeID, MutableSet<NodeDef>> = LazyMap.lazyMap(actualNodeDefs) { -> mutableSetOf() },
        val errorRecoveringNodes: MutableSet<NodeID> = mutableSetOf(),
        var errorCallback: ((UnexpectedTokenError) -> Unit)? = null,
)
