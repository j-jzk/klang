package cz.j_jzk.klang.prales.operators;

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

sealed class ArithmeticOperation<E> {
    /** Identity (a) */
    data class Id<E>(val a: E): ArithmeticOperation<E>()
    /** Addition (a + b) */
    data class Add<E>(val a: ArithmeticOperation<E>, val b: ArithmeticOperation<E>): ArithmeticOperation<E>()
    /** Subtraction (a - b) */
    data class Sub<E>(val a: ArithmeticOperation<E>, val b: ArithmeticOperation<E>): ArithmeticOperation<E>()
    /** Multiplication (a * b) */
    data class Mul<E>(val a: ArithmeticOperation<E>, val b: ArithmeticOperation<E>): ArithmeticOperation<E>()
    /** Division (a / b) */
    data class Div<E>(val a: ArithmeticOperation<E>, val b: ArithmeticOperation<E>): ArithmeticOperation<E>()
    /** Modulo (a % b) */
    data class Mod<E>(val a: ArithmeticOperation<E>, val b: ArithmeticOperation<E>): ArithmeticOperation<E>()
    /** Negation (-a) */
    data class Neg<E>(val a: ArithmeticOperation<E>): ArithmeticOperation<E>()
}

fun <E> arithmetic(expr: NodeID<E>) = lesana<ArithmeticOperation<E>> {
    // from highest priority to lowest priority
    val lvl1 = NodeID<ArithmeticOperation<E>>()
    lvl1 to def(expr) { ArithmeticOperation.Id(it.v1) }

    val lvl2 = NodeID<ArithmeticOperation<E>>()
    lvl2 to def(lvl2, re("\\*"), lvl1) { (a, _, b) -> ArithmeticOperation.Mul(a, b) }
    lvl2 to def(lvl2, re("/"), lvl1) { (a, _, b) -> ArithmeticOperation.Div(a, b) }
    lvl2 to def(lvl2, re("%"), lvl1) { (a, _, b) -> ArithmeticOperation.Mod(a, b) }
    lvl2 to def(lvl1) { it.v1 }

    val lvl3 = NodeID<ArithmeticOperation<E>>()
    lvl3 to def(lvl3, re("\\+"), lvl2) { (a, _, b) -> ArithmeticOperation.Add(a, b) }
    lvl3 to def(lvl3, re("-"), lvl2) { (a, _, b) -> ArithmeticOperation.Sub(a, b) }
    lvl3 to def(re("-"), lvl2) { (_, a) -> ArithmeticOperation.Neg(a) }
    lvl3 to def(lvl2) { it.v1 }

    lvl1 to def(re("\\("), lvl3, re("\\)")) { it.v2 }

    val top = NodeID<ArithmeticOperation<E>>()
    top to def(lvl3) { it.v1 }
    setTopNode(top)
    inheritIgnoredREs()
}
