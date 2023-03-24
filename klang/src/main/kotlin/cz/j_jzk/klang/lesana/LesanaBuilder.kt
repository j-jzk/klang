package cz.j_jzk.klang.lesana

import cz.j_jzk.klang.lex.Lexer
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
import org.apache.commons.collections4.set.CompositeSet
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.lesana.tuple.DataTuple
import cz.j_jzk.klang.lesana.tuple.dataTupleFromList

/**
 * A function for creating a lesana.
 *
 * Typical usage:
 * ```kotlin
 * val lesana = lesana<Int> {
 * 	// create a new ID for representing nodes in the grammar
 * 	val sum = NodeID<Int>()
 *
 * 	// use include() to include predefined parts of the grammar - for example
 * 	// from klang-prales (integer() is an example function returing a LesanaBuilder)
 * 	val number = include(integer())
 *
 * 	// assign a definition to `sum`.
 * 	//  - def() takes as parameters the parts that the grammar item is made of
 * 	//  - re() creates a new grammar item corresponding to the specified regular
 * 	//    expression
 * 	//  - the last parameter of def() is a conversion function which transforms
 * 	//    the nodes specified on the right side of the definition to the resulting
 * 	//    node (the left side, before `to`)
 * 	sum to def(sum, re("\\+"), number) { (a, _, b) -> a + b }
 *
 * 	// a node ID may have multiple definitions associated with it
 * 	sum to def(number) { (num) -> num }
 *
 * 	// use ignoreRegexes() to ignore some specified regular expressions in the
 * 	// input
 * 	ignoreRegexes(" ", "\t", "\n")
 *
 * 	// use setTopNode() to set the top node of the grammar (and of the resulting
 * 	// syntax tree)
 * 	setTopNode(sum)
 *
 * 	// the callback specified by onUnexpectedToken is called whenever a syntax
 * 	// error is found in the input
 * 	onUnexpectedToken { err ->
 * 		println(err)
 * 	}
 * }.getLesana()  // use getLesana() to finalize the definition
 * ```
 */
fun <T> lesana(init: LesanaBuilder<T>.() -> Unit): LesanaBuilder<T> =
		LesanaBuilder<T>().apply { init() }

/**
 * An interface for defining a lesana. You most probably don't want to create
 * this class directly, but instead use the [lesana] function.
 *
 * @param T The type of the final data (the type of the data stored in topNode).
 */
@Suppress("LongParameterList", "TooManyFunctions", "MaxLineLength") // for generated functions
class LesanaBuilder<T> {
	private val lexerDef = LexerDefinition()
	private val parserDef = ParserDefinition<T>()

	/** Creates an epsilon node definition */
	fun <R> def(reduction: (DataTuple.Tuple0) -> R) =
			IntermediateNodeDefinition<R>(listOf(), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	// GENERATED - see tuple.kt
	/** Creates a node definition of 1 element */
	fun <T1, R> def(id1: NodeID<T1>, reduction: (DataTuple.Tuple1<T1>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 2 elements */
	fun <T1, T2, R> def(id1: NodeID<T1>, id2: NodeID<T2>, reduction: (DataTuple.Tuple2<T1, T2>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 3 elements */
	fun <T1, T2, T3, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, reduction: (DataTuple.Tuple3<T1, T2, T3>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 4 elements */
	fun <T1, T2, T3, T4, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, reduction: (DataTuple.Tuple4<T1, T2, T3, T4>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 5 elements */
	fun <T1, T2, T3, T4, T5, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, reduction: (DataTuple.Tuple5<T1, T2, T3, T4, T5>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 6 elements */
	fun <T1, T2, T3, T4, T5, T6, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, reduction: (DataTuple.Tuple6<T1, T2, T3, T4, T5, T6>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5, id6), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 7 elements */
	fun <T1, T2, T3, T4, T5, T6, T7, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, reduction: (DataTuple.Tuple7<T1, T2, T3, T4, T5, T6, T7>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5, id6, id7), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 8 elements */
	fun <T1, T2, T3, T4, T5, T6, T7, T8, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, reduction: (DataTuple.Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5, id6, id7, id8), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 9 elements */
	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, reduction: (DataTuple.Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)
	/** Creates a node definition of 10 elements */
	fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> def(id1: NodeID<T1>, id2: NodeID<T2>, id3: NodeID<T3>, id4: NodeID<T4>, id5: NodeID<T5>, id6: NodeID<T6>, id7: NodeID<T7>, id8: NodeID<T8>, id9: NodeID<T9>, id10: NodeID<T10>, reduction: (DataTuple.Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>) -> R) =
			IntermediateNodeDefinition<R, >(listOf(id1, id2, id3, id4, id5, id6, id7, id8, id9, id10), reduction as (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R)


	/** Maps a node to its definition. */
	infix fun <R> NodeID<R>.to(definition: IntermediateNodeDefinition<R>) {
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

	/**
	 * Defines a function to be called when the lesana encounters an unexpected token.
	 */
	fun onUnexpectedToken(callback: (UnexpectedTokenError) -> Unit) {
		require(parserDef.errorCallback == null) { "Only one `onUnexpectedToken` block is allowed" }
		parserDef.errorCallback = callback
	}

	/** Sets the root node of this lesana */
	fun setTopNode(node: NodeID<T>) {
		parserDef.topNode = node
	}

	/**
	 * Defines a node that corresponds to a regular expression in the input.
	 *
	 * @param regex The regex
	 * @param show When false, the resulting NodeID isn't shown in error messages.
	 */
	fun re(regex: String, show: Boolean = true): NodeID<String> =
		RegexNodeID(regex, show).also { lexerDef.tokenDefs[compileRegex(regex).fa] = it }

	/** Ignore the specified regexes when reading the input */
	fun ignoreRegexes(vararg regexes: String) {
		parserDef.lexerIgnores.addAll(regexes.map(::compileRegex))
	}

	/**
	 * Sets the ignored regexes for this lesana to be inherited from the parent
	 * (includer). This is only useful when this lesana is [include]d somewhere.
	 */
	fun inheritIgnoredREs() {
		parserDef.inheritIgnores = true
	}

	/**
	 * Includes another lesana definition into this one.
	 *
	 * Returns the top ID of the definition, which can then be used in other
	 * node definitions.
	 */
	fun <U> include(subLesana: LesanaBuilder<U>): NodeID<U> {
		lexerDef.include(subLesana.lexerDef)
		parserDef.include(subLesana.parserDef)
		return requireNotNull(subLesana.parserDef.topNode)
	}

	/**
	 * Finalizes the definition and returns the lesana, which can be used to
	 * do the parsing.
	 */
	fun getLesana(): Lesana = Lesana(lexerDef.getLexer(), parserDef.getParser())

	/**
	 * Returns an altered reduction function which:
	 *   - translates the parameters and return values to/from `ASTNode`
	 *   - if any of the nodes to be reduced is `Erroneous`, it also returns
	 *     an Erroneous node, because the reduction couldn't work with it
	 *     (it has no value) and a correct program couldn't be created from
	 *     such an AST anyway
	 */
	private fun <R> wrapReduction(
		nodeID: NodeID<R>,
		reduction: (DataTuple<Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?>) -> R
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
	 *
	 * This class is what the function [def] returns.
	 */
	data class IntermediateNodeDefinition<R>(
		val definition: List<NodeID<Any?>>,
		val reduction: (DataTuple<*,*,*,*,*,*,*,*,*,*>) -> R
	)
}

internal class LexerDefinition {
	val tokenDefs: LinkedHashMap<NFA, NodeID<Any?>> = linkedMapOf()

	fun getLexer() = Lexer(tokenDefs)

	fun include(other: LexerDefinition) {
		tokenDefs.putAll(other.tokenDefs)
	}
}

/**
 * This class stores the parser part of the definition.
 * @param T The type of data in the topNode.
 */
internal class ParserDefinition<T> {
	/** The actual node definition data */
	val actualNodeDefs: MutableMap<NodeID<Any?>, MutableSet<NodeDef>> = mutableMapOf()
	/** Used for simpler code - returns an empty set when a node ID isn't defined */
	val nodeDefs: LazyMap<NodeID<Any?>, MutableSet<NodeDef>> = LazyMap.lazyMap(actualNodeDefs) { -> mutableSetOf() }
	val errorRecoveringNodes: MutableSet<NodeID<Any?>> = mutableSetOf()
	var errorCallback: ((UnexpectedTokenError) -> Unit)? = null
	/** The top node of the grammar (the root of the AST) */
	var topNode: NodeID<T>? = null
	/** Lexer ignores in the current context */
	val lexerIgnores = mutableSetOf<CompiledRegex>()
	var inheritIgnores = false

	/** Builds and returns the parser */
	fun getParser(): DFA {
		val topNodeNotNull = topNode;
		requireNotNull(topNodeNotNull) { "The top node of the grammar must be set" }

		// Add the top node to the error-recovering nodes so the parser doesn't
		// fail completely if the user hasn't specified any error-recovering nodes
		errorRecoveringNodes += topNodeNotNull

		require(topNodeNotNull in actualNodeDefs) { "The top node of the grammar must have at least one definition" }

		return DFABuilder(actualNodeDefs, unifyNode(topNodeNotNull), errorRecoveringNodes.toList(), errorCallback ?: { })
			.build()
	}

	/**
	 * If there are multiple definitions of the node, unify them all and return the resulting node ID.
	 * There must be at least one definition of the node, else this function will fail.
	 */
	private fun <N> unifyNode(node: NodeID<N>): NodeID<N> =
		if (actualNodeDefs[node]!!.size > 1)
			NodeID<N>().also { newTop ->
				nodeDefs[newTop]!! += NodeDef(
					listOf(node),
					{ nodeList ->
						val first = nodeList[0]
						if (first is ASTNode.Data)
							ASTNode.Data(newTop, first.data, first.position)
						else
							ASTNode.Erroneous(newTop, first.position)
					}
				)
			}
		else
			node

	fun include(other: ParserDefinition<*>) {
		if (!other.inheritIgnores)
			nodeDefs.mergeSetValues(other.actualNodeDefs)
		else
			// combine the ignores in the other lesana with this one
			nodeDefs.mergeSetValues(other.actualNodeDefs.mapValues { (_, set) ->
				set.map { NodeDef(it.elements, it.reduction, CompositeSet(this.lexerIgnores, it.lexerIgnores)) }.toSet()
			})

		errorRecoveringNodes.addAll(other.errorRecoveringNodes)
	}
}
