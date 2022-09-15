package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lex.api.lexer
import cz.j_jzk.klang.parse.ASTNode
import cz.j_jzk.klang.parse.NodeID
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
            assertNodeValueEquals("$i", iterator.next(listOf("int")))
	}

    @Test fun testPushback() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        val node = ASTNode.Data(NodeID.ID("int"), "99", PositionInfo("input", 123))

        iterator.pushback(node)
        assertEquals(node, iterator.next(listOf("int")))

        for (i in 1..3)
            iterator.next(listOf("int"))

        iterator.pushback(node)
        assertEquals(node, iterator.next(listOf("int")))
    }

    @Test fun testHasNext() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        for (i in 1..4) {
            assertTrue(iterator.hasNext())
            iterator.next(listOf("int"))
        }

        assertFalse(iterator.hasNext())
    }

    @Test fun testEof() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        for (i in 1..3)
            assertIsNot<NodeID.Eof>(iterator.next(listOf("int"))!!.id)

        assertTrue(iterator.hasNext())
        assertIs<NodeID.Eof>(iterator.next(listOf("int"))!!.id)
        assertEquals(null, iterator.next(listOf("int")))
        assertFalse(iterator.hasNext())
    }

    @Test fun testPeek() {
        val iterator = LexerPPPIterator(lexerWrapper, createInput())
        assertNodeValueEquals("1", iterator.peek(listOf("int")))
        assertNodeValueEquals("1", iterator.peek(listOf("int")))
        assertNodeValueEquals("1", iterator.next(listOf("int")))
        assertNodeValueEquals("2", iterator.peek(listOf("int")))

        // test pushback & peek
        val node = ASTNode.Data(NodeID.ID("int"), "99", PositionInfo("input", 123))
        iterator.pushback(node)
        assertEquals(node, iterator.peek(listOf("int")))
    }

    private val lexerWrapper = lexer {
        ignore(" ")
        "int" to "\\d+"
    }.getLexer()
    private fun createInput() = InputFactory.fromString("1 2 3", "input")
    private fun assertNodeValueEquals(expectedVal: String, node: ASTNode?) {
        assertNotNull(node)
        val dataNode = assertIs<ASTNode.Data>(node)
        assertEquals(expectedVal, dataNode.data)
    }
}
