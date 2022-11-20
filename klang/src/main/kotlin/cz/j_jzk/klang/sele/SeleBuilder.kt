package cz.j_jzk.klang.sele

import cz.j_jzk.klang.lex.Lexer
import cz.j_jzk.klang.lex.LexerWrapper
import cz.j_jzk.klang.lex.re.CompiledRegex
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
import cz.j_jzk.klang.sele.tuple.DataTuple
import cz.j_jzk.klang.sele.tuple.dataTupleFromList

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
	// fun def(vararg definition: NodeID, reduction: (List<*>) -> Any) =
	// 	IntermediateNodeDefinition(definition.toList(), reduction)

	fun <T1, R> def(id1: NodeID<T1>, reduction: (DataTuple.Tuple1<T1>) -> R) =
		IntermediateNodeDefinition<T1, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple1<T1>, R>(listOf(id1), reduction)
//	fun <T1, T2, R> def(id1: NodeID<T1>, id2: NodeID<T2>, reduction: (DataTuple.Tuple2<T1, T2>) -> R) =
//		IntermediateNodeDefinition<T1, T2, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple2<T1, T2>, R>(listOf(id1, id2), reduction)
//	fun <T1, T2, T3, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, reduction: (DataTuple.Tuple3<T1, T2, T3>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple3<T1, T2, T3>, R>(listOf(id1, id2, id3), reduction)
//	fun <T1, T2, T3, T4, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, reduction: (DataTuple.Tuple4<T1, T2, T3, T4>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple4<T1, T2, T3, T4>, R>(listOf(id1, id2, id3, id4), reduction)
//	fun <T1, T2, T3, T4, T5, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, reduction: (DataTuple.Tuple5<T1, T2, T3, T4, T5>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple5<T1, T2, T3, T4, T5>, R>(listOf(id1, id2, id3, id4, id5), reduction)
//	fun <T1, T2, T3, T4, T5, T6, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, reduction: (DataTuple.Tuple6<T1, T2, T3, T4, T5, T6>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple6<T1, T2, T3, T4, T5, T6>, R>(listOf(id1, id2, id3, id4, id5, id6), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, reduction: (DataTuple.Tuple7<T1, T2, T3, T4, T5, T6, T7>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple7<T1, T2, T3, T4, T5, T6, T7>, R>(listOf(id1, id2, id3, id4, id5, id6, id7), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, reduction: (DataTuple.Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, reduction: (DataTuple.Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, reduction: (DataTuple.Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, reduction: (DataTuple.Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, reduction: (DataTuple.Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, reduction: (DataTuple.Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, reduction: (DataTuple.Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, reduction: (DataTuple.Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Nothing, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, id16: NodeID<T16>, reduction: (DataTuple.Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Nothing, Nothing, Nothing, Nothing, DataTuple.Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15, id16), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, id16: NodeID<T16>, id17: NodeID<T17>, reduction: (DataTuple.Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, Nothing, Nothing, Nothing, DataTuple.Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15, id16, id17), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, id16: NodeID<T16>, id17: NodeID<T17>, id18: NodeID<T18>, reduction: (DataTuple.Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, Nothing, Nothing, DataTuple.Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15, id16, id17, id18), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, id16: NodeID<T16>, id17: NodeID<T17>, id18: NodeID<T18>, id19: NodeID<T19>, reduction: (DataTuple.Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, Nothing, DataTuple.Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15, id16, id17, id18, id19), reduction)
//	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, id11: NodeID<T11>, id12: NodeID<T12>, id13: NodeID<T13>, id14: NodeID<T14>, id15: NodeID<T15>, id16: NodeID<T16>, id17: NodeID<T17>, id18: NodeID<T18>, id19: NodeID<T19>, id20: NodeID<T20>, reduction: (DataTuple.Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>) -> R) =
//		IntermediateNodeDefinition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, DataTuple.Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, R>(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12, id13, id14, id15, id16, id17, id18, id19, id20), reduction)

	/** Maps a node to its definition. */
	infix fun <R> NodeID<R>.to(
		definition: IntermediateNodeDefinition<
			Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?,
			DataTuple<Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, >,
			R
		>
	) {
		val actualReduction = wrapReduction(this, definition.reduction)
		parserDef.nodeDefs[this]!!.add(NodeDef(definition.definition, actualReduction, parserDef.lexerIgnores))
	}

	/**
	 * Marks `nodes` to be error-recovering. These nodes will be then used to
	 * contain syntax errors.
	 */
	fun errorRecovering(vararg nodes: NodeID<Any?>) {
		parserDef.errorRecoveringNodes.addAll(nodes)
	}

	/** Sets the root node of this sele */
	fun setTopNode(node: NodeID<Any?>) {
		parserDef.topNode = node
	}

	/** Defines a regex node */
	fun re(regex: String): NodeID<String> =
		RegexNodeID(regex).also { lexerDef.tokenDefs[compileRegex(regex).fa] = it }

	/** Ignore the specified regexes when reading the input */
	fun ignoreRegexes(vararg regexes: String) {
		parserDef.lexerIgnores.addAll(regexes.map(::compileRegex))
	}

	/**
	 * Includes another sele definition into this one.
	 * Returns the top ID of the definition, which can then be used in other
	 * node definitions.
	 */
	fun include(subSele: SeleBuilder): NodeID<Any?> { // FIXME: type safety for includes
		lexerDef.include(subSele.lexerDef)
		parserDef.include(subSele.parserDef)
		return requireNotNull(subSele.parserDef.topNode)
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
	private fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>
	wrapReduction(
		nodeID: NodeID<R>,
		reduction: (DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >) -> R
	): (List<ASTNode>) -> ASTNode = { nodeList ->
		if (nodeList.none { it is ASTNode.Erroneous })
			ASTNode.Data(
					nodeID,
					reduction(
						dataTupleFromList(
							nodeList.map { node ->
								when (node) {
									is ASTNode.Data -> node.data
									is ASTNode.NoValue -> null
									else -> throw IllegalStateException("This should never happen")
								}
							}
						)
					),

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
	data class IntermediateNodeDefinition<
		T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20,
		U: DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>,
		R
	>(val definition: List<NodeID<Any?>>, val reduction: (U) -> R)
}

internal class LexerDefinition {
	val tokenDefs: LinkedHashMap<NFA, NodeID<Any?>> = linkedMapOf()

	var unexpectedCharacterHandler: ((Char, PositionInfo) -> Unit)? = null
	fun getLexer() = LexerWrapper(Lexer(tokenDefs, /* ignored */), unexpectedCharacterHandler ?: { _, _ -> })

	fun include(other: LexerDefinition) {
		tokenDefs.putAll(other.tokenDefs)
		// TODO: include ignored regexes
	}
}

internal class ParserDefinition {
	/** The actual node definition data */
	val actualNodeDefs: MutableMap<NodeID<Any?>, MutableSet<NodeDef>> = mutableMapOf()
	/** Used for simpler code - returns an empty set when a node ID isn't defined */
	val nodeDefs: LazyMap<NodeID<Any?>, MutableSet<NodeDef>> = LazyMap.lazyMap(actualNodeDefs) { -> mutableSetOf() }
	val errorRecoveringNodes: MutableSet<NodeID<Any?>> = mutableSetOf()
	var errorCallback: ((UnexpectedTokenError) -> Unit)? = null
	/** The top node of the grammar (the root of the AST) */
	var topNode: NodeID<Any?>? = null
	/** Lexer ignores in the current context */
	val lexerIgnores = mutableSetOf<CompiledRegex>()

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
