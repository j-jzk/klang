package cz.j_jzk.klang.lesana

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.parse.UnexpectedCharacter
import cz.j_jzk.klang.parse.EOFNodeID
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.testutils.assertElementsEqual

class LesanaTest {
    @Test fun testUnexpectedCharacter() {
        val lesana = lesana<Int> {
            val int = NodeID<Int>()
            int to def(re("\\d+")) { it.v1.toInt() }
            setTopNode(int)
        }.getLesana()

        assertFailsWith<SyntaxError> { lesana.parse(InputFactory.fromString("abc", "")) }
    }

    @Test fun testErrorCallback() {
        var numberOfErrors = 0
        val lesana = lesana<Int> {
            val int = NodeID<Int>()
            int to def(re("\\d+")) { it.v1.toInt() }
            val top = NodeID<Int>()
            top to def() { 0 }
            top to def(top, int) { (t, i) -> t + i }

            setTopNode(top)
            errorRecovering(int)
            ignoreRegexes("\\s")

            onUnexpectedToken { err ->
                println(err) // TODO: test the error string
                assertEquals(UnexpectedCharacter, err.got.id)
                assertElementsEqual(setOf(int, EOFNodeID, RegexNodeID("\\d+")), err.expectedIDs)
                numberOfErrors++
            }
        }.getLesana()

        assertFailsWith<SyntaxError> { lesana.parse(InputFactory.fromString("1 2 1a1a34a5", "")) }
        assertEquals(3, numberOfErrors)
    }
}
