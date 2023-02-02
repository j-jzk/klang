package cz.j_jzk.klang.parse

import cz.j_jzk.klang.util.PositionInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnexpectedTokenErrorTest {
    private val aId = NodeID<Int>("a")
    private val bId = NodeID<Int>("b")
    private val cId = NodeID<Int>("c")
    private val unnamedId = NodeID<Int>()
    private val hiddenId = NodeID<Int>("d", false)

    @Test fun testBasic() {
        assertEquals(
            "Unexpected token a, expected one of: [b, c].",
            UnexpectedTokenError(ASTNode.NoValue(aId, PositionInfo("", 0)), listOf(bId, cId)).toString(),
        )
    }

    @Test fun testUnnamed() {
        assertTrue (
            Regex(
                "Unexpected token a, expected one of: \\[b, cz\\.j_jzk\\.klang\\.parse\\.NodeID@[0-9a-f]+]\\."
            ).matches(
                UnexpectedTokenError(ASTNode.NoValue(aId, PositionInfo("", 0)), listOf(bId, unnamedId))
                    .toString(),
            )
        )
    }

    @Test fun testHidden() {
        assertEquals(
            "Unexpected token a, expected one of: [b].",
            UnexpectedTokenError(ASTNode.NoValue(aId, PositionInfo("", 0)), listOf(bId, hiddenId)).toString(),
        )
    }

    @Test fun testUnexpectedHidden() {
        assertEquals(
            "Unexpected token d, expected one of: [a, b].",
            UnexpectedTokenError(ASTNode.NoValue(hiddenId, PositionInfo("", 0)), listOf(aId, bId)).toString(),
        )
    }
}
