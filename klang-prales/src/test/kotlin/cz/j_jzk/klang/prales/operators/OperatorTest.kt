package cz.j_jzk.klang.prales.operators

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

class OperatorTest {
    @Test fun testArithmetic() {
        val lesana = lesana<Oper<Int>> {
            val int = NodeID<Int>()
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val operators = include(operators(int, true, false))
            setTopNode(operators)
            ignoreRegexes("\\s+")
        }

        testLesana(
            lesana.getLesana(),
            mapOf(
                "1" to i(1),
                "1 + 2" to add(i(1), i(2)),
                "1 - 2" to sub(i(1), i(2)),
                "1 / 2" to div(i(1), i(2)),
                "1 * 2" to mul(i(1), i(2)),
                "1 % 2" to mod(i(1), i(2)),
                "-1" to neg(i(1)),
                "(1)" to i(1),
                "(((1)))" to i(1),

                "1 * 2 + 3" to add(mul(i(1), i(2)), i(3)),
                "1 + 2 * 3" to add(i(1), mul(i(2), i(3))),
                "1 * 2 / 3 * 4 / 5" to div(mul(div(mul(i(1), i(2)), i(3)), i(4)), i(5)),
                "1 - 2 - 3" to sub(sub(i(1), i(2)), i(3)),
                "(1 + 2) * 3" to mul(add(i(1), i(2)), i(3)),
                "- 1 + 2" to add(neg(i(1)), i(2)),
                "1 + (-2)" to add(i(1), neg(i(2))),
                "-1 * 2" to neg(mul(i(1), i(2))),
            ),
            listOf(
                "1 + -2",
                "2 * -1",
                "1 ++ 2",
                "1+",
                "+1",
                "()",
                "(1",
                "1)",
                "1 && 2",
                "!1",
                "1 || 2",
            ),
        )
    }

    @Test fun testLogic() {
        val lesana = lesana<Oper<Int>> {
            val int = NodeID<Int>()
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val operators = include(operators(int, false, true))
            setTopNode(operators)
            ignoreRegexes("\\s+")
        }

        testLesana(
            lesana.getLesana(),
            mapOf(
                "1" to i(1),

                "1 && 2" to and(i(1), i(2)),
                "1 || 2" to or(i(1), i(2)),
                "!1" to not(i(1)),

                "1 && 2 || 3" to or(and(i(1), i(2)), i(3)),
                "1 || 2 && 3" to or(i(1), and(i(2), i(3))),
                "(1 || 2) && 3" to and(or(i(1), i(2)), i(3)),
                "1 && 2 || !3" to or(and(i(1), i(2)), not(i(3)))


            ),
            listOf(
                "1 + -2",
                "2 * -1",
                "1 ++ 2",
                "1+",
                "+1",
                "()",
                "(1",
                "1)",
                "1 + 2",
                "1 * 2",
                "-1",
                "1 / 2",
                "1 * 2",
                "i % 2",
            ),
        )
    }

    @Test fun testAll() {
        val lesana = lesana<Oper<Int>> {
            val int = NodeID<Int>()
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val operators = include(operators(int, true, true))
            setTopNode(operators)
            ignoreRegexes("\\s+")
        }

        testLesana(
            lesana.getLesana(),
            mapOf(
                "1" to i(1),
                "1 && 2" to and(i(1), i(2)),
                "1 || 2" to or(i(1), i(2)),
                "!1" to not(i(1)),

                "1 && 2 || 3" to or(and(i(1), i(2)), i(3)),
                "1 || 2 && 3" to or(i(1), and(i(2), i(3))),
                "(1 || 2) && 3" to and(or(i(1), i(2)), i(3)),
                "1 && 2 || !3" to or(and(i(1), i(2)), not(i(3))),

                "1 + 2" to add(i(1), i(2)),
                "1 - 2" to sub(i(1), i(2)),
                "1 / 2" to div(i(1), i(2)),
                "1 * 2" to mul(i(1), i(2)),
                "1 % 2" to mod(i(1), i(2)),
                "-1" to neg(i(1)),
                "(1)" to i(1),
                "(((1)))" to i(1),

                "1 * 2 + 3" to add(mul(i(1), i(2)), i(3)),
                "1 + 2 * 3" to add(i(1), mul(i(2), i(3))),
                "1 * 2 / 3 * 4 / 5" to div(mul(div(mul(i(1), i(2)), i(3)), i(4)), i(5)),
                "1 - 2 - 3" to sub(sub(i(1), i(2)), i(3)),
                "(1 + 2) * 3" to mul(add(i(1), i(2)), i(3)),
                "- 1 + 2" to add(neg(i(1)), i(2)),
                "1 + (-2)" to add(i(1), neg(i(2))),
                "-1 * 2" to neg(mul(i(1), i(2))),

                "1 || 2 && !3 + 4 * 5" to or(i(1), and(i(2), not(add(i(3), mul(i(4), i(5)))))),
            ),
            listOf(
                "1 + -2",
                "2 * -1",
                "1 ++ 2",
                "1+",
                "+1",
                "()",
                "(1",
                "1)",
            ),
        )
    }

    private fun i(value: Int) = Oper.Id(value)
    private fun add(a: Oper<Int>, b: Oper<Int>) = Oper.Arit.Add(a, b)
    private fun sub(a: Oper<Int>, b: Oper<Int>) = Oper.Arit.Sub(a, b)
    private fun mul(a: Oper<Int>, b: Oper<Int>) = Oper.Arit.Mul(a, b)
    private fun div(a: Oper<Int>, b: Oper<Int>) = Oper.Arit.Div(a, b)
    private fun mod(a: Oper<Int>, b: Oper<Int>) = Oper.Arit.Mod(a, b)
    private fun neg(a: Oper<Int>) = Oper.Arit.Neg(a)
    private fun not(a: Oper<Int>) = Oper.Log.Not(a)
    private fun and(a: Oper<Int>, b: Oper<Int>) = Oper.Log.And(a, b)
    private fun or(a: Oper<Int>, b: Oper<Int>) = Oper.Log.Or(a, b)
}
