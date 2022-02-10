package cz.j_jzk.klang.parse.algo

import kotlin.test.Test
import kotlin.test.assertEquals
import com.google.common.collect.Table
import com.google.common.collect.HashBasedTable
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeDef
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.util.set
import cz.j_jzk.klang.parse.testutil.*

/* TODO: make this less hacky
 * Specifically, find a way to structurally compare DFAs (this class currently
 * relies on the iteration order of sets, which is undefined) */
class DFABuilderTest {
	private val leftRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e2, p, e), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	private val rightRecursiveGrammar: Map<NodeID, Set<NodeDef<ASTData>>> = mapOf(
		top to setOf(NodeDef(listOf(e2), topReduction)),
		e2 to setOf(
			NodeDef(listOf(e, p, e2), exprReduction),
			NodeDef(listOf(e), exprReduction)
		)
	)

	@Test fun testBasicConstruction() {
		val dfa = DFABuilder(leftRecursiveGrammar, top, emptyList()).build()
		assertEquals(leftRecursiveDFA, dfa)
	}

	@Test fun testRightRecursion() {
		val dfa = DFABuilder(rightRecursiveGrammar, top, emptyList()).build()
		assertEquals(rightRecursiveDFA, dfa)
	}

	@Test fun testLeftRecusionWithErrorRecovery() {
		val dfa = DFABuilder(leftRecursiveGrammar, top, listOf(e2, top)).build()
		assertEquals(errorHandlingLeftRecursiveDFA, dfa)
	}

	@Test fun testRightRecursionWithErrorRecovery() {
		val dfa = DFABuilder(rightRecursiveGrammar, top, listOf(e2, top)).build()
		assertEquals(errorHandlingRightRecursiveDFA, dfa)
	}
}
