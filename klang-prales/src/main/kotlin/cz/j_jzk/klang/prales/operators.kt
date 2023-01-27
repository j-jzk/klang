package cz.j_jzk.klang.prales.operators;

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

sealed class Operation<E> {
    /** Identity (a) */
    data class Id<E>(val a: E): Operation<E>()
    /** Addition (a + b) */
    data class Add<E>(val a: Operation<E>, val b: Operation<E>): Operation<E>()
    /** Subtraction (a - b) */
    data class Sub<E>(val a: Operation<E>, val b: Operation<E>): Operation<E>()
    /** Multiplication (a * b) */
    data class Mul<E>(val a: Operation<E>, val b: Operation<E>): Operation<E>()
    /** Division (a / b) */
    data class Div<E>(val a: Operation<E>, val b: Operation<E>): Operation<E>()
    /** Modulo (a % b) */
    data class Mod<E>(val a: Operation<E>, val b: Operation<E>): Operation<E>()
    /** Negation (-a) */
    data class Neg<E>(val a: Operation<E>): Operation<E>()
}

fun <E> operators(expr: NodeID<E>) = lesana<Operation<E>> {
    // from highest priority to lowest priority
    val lvl1 = NodeID<Operation<E>>()
    lvl1 to def(expr) { Operation.Id(it.v1) }

    val lvl2 = NodeID<Operation<E>>()
    lvl2 to def(lvl2, re("\\*"), lvl1) { (a, _, b) -> Operation.Mul(a, b) }
    lvl2 to def(lvl2, re("/"), lvl1) { (a, _, b) -> Operation.Div(a, b) }
    lvl2 to def(lvl2, re("%"), lvl1) { (a, _, b) -> Operation.Mod(a, b) }
    lvl2 to def(lvl1) { it.v1 }

    val lvl3 = NodeID<Operation<E>>()
    lvl3 to def(lvl3, re("\\+"), lvl2) { (a, _, b) -> Operation.Add(a, b) }
    lvl3 to def(lvl3, re("-"), lvl2) { (a, _, b) -> Operation.Sub(a, b) }
    lvl3 to def(re("-"), lvl2) { (_, a) -> Operation.Neg(a) }
    lvl3 to def(lvl2) { it.v1 }

    lvl1 to def(re("\\("), lvl3, re("\\)")) { it.v2 }

    val top = NodeID<Operation<E>>()
    top to def(lvl3) { it.v1 }
    setTopNode(top)
    inheritIgnoredREs()
}
