package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.lex.api.AnyNodeID
import cz.j_jzk.klang.lex.re.compileRegex
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.util.PositionInfo
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertIsNot

class LexerPPPIteratorTest {
	@Test fun testNext() {
		val iterator = LexerPPPIterator(lexerWrapper, createInput())
        for (i in 1..3)
            assertNodeValueEquals("$i", iterator.next(intList, ignoreSpace))
	}

    @Test fun testPushback() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        val node = ASTNode.Data(AnyNodeID("int"), "99", PositionInfo("input", 123))

        iterator.pushback(node)
        assertEquals(node, iterator.next(intList, ignoreSpace))

        for (i in 1..3)
            iterator.next(intList, ignoreSpace)

        iterator.pushback(node)
        assertEquals(node, iterator.next(intList, ignoreSpace))
    }

    @Test fun testHasNext() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        for (i in 1..4) {
            assertTrue(iterator.hasNext())
            iterator.next(intList, ignoreSpace)
        }

        assertFalse(iterator.hasNext())
    }

    @Test fun testEof() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        for (i in 1..3)
            assertIsNot<EOFNodeID>(iterator.next(intList, ignoreSpace)!!.id)

        assertTrue(iterator.hasNext())
        assertIs<EOFNodeID>(iterator.next(intList, ignoreSpace)!!.id)
        assertEquals(null, iterator.next(intList, ignoreSpace))
        assertFalse(iterator.hasNext())
    }

    @Test fun testPeek() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        assertNodeValueEquals("1", iterator.peek(intList, ignoreSpace))
        assertNodeValueEquals("1", iterator.peek(intList, ignoreSpace))
        assertNodeValueEquals("1", iterator.next(intList, ignoreSpace))
        assertNodeValueEquals("2", iterator.peek(intList, ignoreSpace))

        // test pushback & peek
        val node = ASTNode.Data(AnyNodeID("int"), "99", PositionInfo("input", 123))
        iterator.pushback(node)
        assertEquals(node, iterator.peek(intList, ignoreSpace))
    }

    private val lexerWrapper = lexer {
        ignore(" ")
        "int" to "\\d+"
    }.getLexer()
    private val intList = listOf(AnyNodeID("int"))
    private val ignoreSpace = setOf(compileRegex(" ").fa)
    private fun createInput() = InputFactory.fromString("1 2 3", "input")
    private fun assertNodeValueEquals(expectedVal: String, node: ASTNode?) {
        assertNotNull(node)
        val dataNode = assertIs<ASTNode.Data>(node)
        assertEquals(expectedVal, dataNode.data)
    }
}
