package cz.j_jzk.klang.prales.operators;

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

sealed class ArithmeticOperation<E> {
    /** Just a. */
    data class Identity<E>(val a: E): ArithmeticOperation<E>()
    /** a + b */
    data class Addition<E>(val a: E, val b: E): ArithmeticOperation<E>()
    /** a - b */
    data class Subtraction<E>(val a: E, val b: E): ArithmeticOperation<E>()
    /** a * b */
    data class Multiplication<E>(val a: E, val b: E): ArithmeticOperation<E>()
    /** a / b */
    data class Division<E>(val a: E, val b: E): ArithmeticOperation<E>()
    /** a % b */
    data class Modulo<E>(val a: E, val b: E): ArithmeticOperation<E>()
    /** -a */
    data class Negation<E>(val a: E): ArithmeticOperation<E>()
}

fun <E> arithmetic(expr: NodeID<E>) = lesana<ArithmeticOperation<E>> {
    // from highest priority to lowest priority
    val lvl1 = NodeID<ArithmeticOperation<E>>()
    lvl1 to def(expr) { ArithmeticOperation.Identity(it.v1) }

    val lvl2 = NodeID<ArithmeticOperation<E>>()
    lvl2 to def(lvl2, re("\\*"), lvl1) { (a, _, b) -> ArithmeticOperation.Multiplication(a, b) }
    lvl2 to def(lvl2, re("/"), lvl1) { (a, _, b) -> ArithmeticOperation.Division(a, b) }
    lvl2 to def(lvl2, re("%"), lvl1) { (a, _, b) -> ArithmeticOperation.Modulo(a, b) }
    lvl2 to def(lvl1) { it.v1 }

    val lvl3 = NodeID<ArithmeticOperation<E>>()
    lvl3 to def(lvl3, re("\\+"), lvl2) { (a, _, b) -> ArithmeticOperation.Addition(a, b) }
    lvl3 to def(lvl3, re("-"), lvl2) { (a, _, b) -> ArithmeticOperation.Subtraction(a, b) }
    lvl3 to def(re("-"), lvl2) { (_, a) -> ArithmeticOperation.Negation(a) }
    lvl3 to def(lvl2) { it.v1 }

    lvl1 to def(re("\\("), lvl3, re("\\)")) { it.v2 }

    val top = NodeID<ArithmeticOperation<E>>()
    top to def(lvl3) { it.v1 }
    setTopNode(top)
}
