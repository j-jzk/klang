package cz.j_jzk.klang.lesana

import kotlin.test.Test
import kotlin.test.assertFailsWith
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.input.InputFactory

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
        val lesana = lesana<Int> {

        }
    }
}
