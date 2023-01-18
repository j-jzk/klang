package cz.j_jzk.klang.prales.operators

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

class ArithmeticTest {
    @Test fun testBasic() {
        val lesana = lesana<ArithmeticOperation<Int>> {
            val int = NodeID<Int>()
            int to def(re("[0-9]+")) { it.v1.toInt() }
            val operators = include(arithmetic(int))
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

                "1 * 2 + 3" to add(mul(i(1), i(2)), i(3)),
                "1 + 2 * 3" to add(i(1), mul(i(2), i(3))),
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
            ),
        )
    }

    private fun i(value: Int) = ArithmeticOperation.Id(value)
    private fun add(a: ArithmeticOperation<Int>, b: ArithmeticOperation<Int>) = ArithmeticOperation.Add(a, b)
    private fun sub(a: ArithmeticOperation<Int>, b: ArithmeticOperation<Int>) = ArithmeticOperation.Sub(a, b)
    private fun mul(a: ArithmeticOperation<Int>, b: ArithmeticOperation<Int>) = ArithmeticOperation.Mul(a, b)
    private fun div(a: ArithmeticOperation<Int>, b: ArithmeticOperation<Int>) = ArithmeticOperation.Div(a, b)
    private fun mod(a: ArithmeticOperation<Int>, b: ArithmeticOperation<Int>) = ArithmeticOperation.Mod(a, b)
    private fun neg(a: ArithmeticOperation<Int>) = ArithmeticOperation.Neg(a)
}
