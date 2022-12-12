package cz.j_jzk.klang.sele

import kotlin.test.Test
import kotlin.test.assertFailsWith
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.parse.SyntaxError
import cz.j_jzk.klang.input.InputFactory

class SeleTest {
    @Test fun testUnexpectedCharacter() {
        val sele = sele<Int> {
            val int = NodeID<Int>()
            int to def(re("\\d+")) { it.v1.toInt() }
            setTopNode(int)
        }.getSele()

        assertFailsWith<SyntaxError> { sele.parse(InputFactory.fromString("abc", "")) }
    }
}
