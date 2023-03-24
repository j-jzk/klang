package cz.j_jzk.klang.prales.operators;

import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID

/**
 * Defines arithmetic and/or logic operations on the specified NodeID.
 *
 * The resulting nodes are of the type [Oper] (see the class' documentation).
 * Because of the type system, raw nodes are wrapped in the special operation
 * [Oper.Id] (identity). For example, if `expr` is `NodeID<Int>`, then a `123` in
 * the source becomes `Oper.Id<Int>(123)`.
 *
 * The following operators are supported:
 *  - **arithmetic**: `a+b`, `a-b`, `-a`, `a*b`, `a/b`, `a%b`
 *  - **logic**: `a && b`, `a || b`, `!a`
 *
 * Operator priority works in accordance to convention:
 *  1. `a*b`, `a/b`, `a%b`
 *  2. `a+b`, `a-b`, `-a`
 *  3. `!a`
 *  4. `a && b`
 *  5. `a || b`
 * 
 * Parenthesization can be used to override operator priority: `(a+b)*c`
 *
 * All the operators are left-associative, meaning:
 *  - a+b+c = (a+b)+c
 *  - a+b-c+d = ((a+b)-c)+d
 *
 * @param E The data type of the operands
 * @param expr The node ID to define the operations on
 * @param arithmetic Whether to enable arithmetic operations
 * @param logic Whether to enable logic operations
 */
fun <E> operators(expr: NodeID<E>, arithmetic: Boolean = true, logic: Boolean = true) = lesana<Oper<E>> {
    // from highest to lowest priority
    // ARITHMETIC OPERATIONS
    val lvl1 = NodeID<Oper<E>>(show=false)
    lvl1 to def(expr) { Oper.Id(it.v1) }

    val aritTop = if (arithmetic) {
        val lvl2 = NodeID<Oper<E>>(show=false)
        lvl2 to def(lvl2, re("\\*"), lvl1) { (a, _, b) -> Oper.Arit.Mul(a, b) }
        lvl2 to def(lvl2, re("/"), lvl1) { (a, _, b) -> Oper.Arit.Div(a, b) }
        lvl2 to def(lvl2, re("%"), lvl1) { (a, _, b) -> Oper.Arit.Mod(a, b) }
        lvl2 to def(lvl1) { it.v1 }

        val lvl3 = NodeID<Oper<E>>("arithmetic operation")
        lvl3 to def(lvl3, re("\\+"), lvl2) { (a, _, b) -> Oper.Arit.Add(a, b) }
        lvl3 to def(lvl3, re("-"), lvl2) { (a, _, b) -> Oper.Arit.Sub(a, b) }
        lvl3 to def(re("-"), lvl2) { (_, a) -> Oper.Arit.Neg(a) }
        lvl3 to def(lvl2) { it.v1 }

        lvl3
    } else {
        lvl1
    }

    // LOGIC OPERATIONS
    val logTop = if (logic) {
        val lvl4 = NodeID<Oper<E>>(show=false)
        lvl4 to def(re("!"), aritTop) { (_, a) -> Oper.Log.Not(a) }
        lvl4 to def(aritTop) { it.v1 }

        val lvl5 = NodeID<Oper<E>>(show=false)
        lvl5 to def(lvl5, re("&&"), lvl4) { (a, _, b) -> Oper.Log.And(a, b) }
        lvl5 to def(lvl4) { it.v1 }

        val lvl6 = NodeID<Oper<E>>("logic operation")
        lvl6 to def(lvl6, re("\\|\\|"), lvl5) { (a, _, b) -> Oper.Log.Or(a, b) }
        lvl6 to def(lvl5) { it.v1 }

        lvl6
    } else {
        aritTop
    }

    lvl1 to def(re("\\("), logTop, re("\\)")) { it.v2 }

    val top = NodeID<Oper<E>>(show=false)
    top to def(logTop) { it.v1 }
    setTopNode(top)
    inheritIgnoredREs()
}

/**
 * A class used to express arithmetic and logic operations.
 *
 * Arithmetic operations are namespaced to the sub-object [Oper.Arit].
 * Logic operations are namespaced to the sub-object [Oper.Log].
 *
 * The root-level subclass [Oper.Id] (identity) is used for encapsulating literal
 * operands, i.e. operands that aren't composed of other operations.
 */
sealed class Oper<E> {
    /** Identity (a) */
    data class Id<E>(val a: E): Oper<E>()

    /** A namespace for arithmetic operations */
    object Arit {
        /** Addition (a + b) */
        data class Add<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()

        /** Subtraction (a - b) */
        data class Sub<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()

        /** Multiplication (a * b) */
        data class Mul<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()

        /** Division (a / b) */
        data class Div<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()

        /** Modulo (a % b) */
        data class Mod<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()

        /** Negation (-a) */
        data class Neg<E>(val a: Oper<E>): Oper<E>()
    }

    /** A namespace for logic operations */
    object Log {
        /** Negation (!a) */
        data class Not<E>(val a: Oper<E>): Oper<E>()
        /** Conjunction (a && b) */
        data class And<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()
        /** Disjunction (a || b) */
        data class Or<E>(val a: Oper<E>, val b: Oper<E>): Oper<E>()
    }
}
