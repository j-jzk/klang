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
import cz.j_jzk.klang.testutils.iter

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
                assertEquals("Unexpected character 'a'", err.toString())
                assertEquals(UnexpectedCharacter, err.got.id)
                assertElementsEqual(setOf(int, EOFNodeID, RegexNodeID("\\d+")), err.expectedIDs)
                numberOfErrors++
            }
        }.getLesana()

        assertFailsWith<SyntaxError> { lesana.parse(InputFactory.fromString("1 2 1a1a34a5", "")) }
        assertEquals(3, numberOfErrors)
    }

    // Regression tests for #83
    @Test fun testIgnoresAfterIncluded() {
        val sub = lesana<Int> {
            val int = NodeID<Int>()
            int to def(re("[0-9]")) { it.v1.toInt() }
            setTopNode(int)
        }

        val sup = lesana<Int> {
            val twoInts = NodeID<Int>()
            val int = include(sub)
            twoInts to def(int, int) { (a, b) -> a + b }
            setTopNode(twoInts)
            ignoreRegexes(" ")
        }.getLesana()

        assertEquals(3, sup.parse(iter("12")))
        assertEquals(3, sup.parse(iter("1 2")))
    }

    @Test fun testIgnoresAfterIncludedNested() {
        val sub = lesana<Int> {
            val int = NodeID<Int>()
            int to def(re("[0-9]")) { it.v1.toInt() }
            val int2 = NodeID<Int>()
            int2 to def(int) { it.v1 }
            setTopNode(int2)
        }

        val sup = lesana<Int> {
            val twoInts = NodeID<Int>()
            val int = include(sub)
            twoInts to def(int, int) { (a, b) -> a + b }
            setTopNode(twoInts)
            ignoreRegexes(" ")
        }.getLesana()

        assertEquals(3, sup.parse(iter("12")))
        assertEquals(3, sup.parse(iter("1 2")))
    }

    @Test fun testIgnoresAfterIncludedNullable() {
        val sub = lesana<Int> {
            val int = NodeID<Int>()
            val optA = NodeID<Unit>()
            int to def(re("[0-9]"), optA) { it.v1.toInt() }
            optA to def(re("a")) { }
            optA to def() { }
            setTopNode(int)
        }

        val sup = lesana<Int> {
            val twoInts = NodeID<Int>()
            val int = include(sub)
            twoInts to def(int, int) { (a, b) -> a + b }
            setTopNode(twoInts)
            ignoreRegexes(" ")
        }.getLesana()

        assertEquals(3, sup.parse(iter("12")))
        assertEquals(3, sup.parse(iter("1 2")))
        assertEquals(3, sup.parse(iter("1a 2")))
    }
}
