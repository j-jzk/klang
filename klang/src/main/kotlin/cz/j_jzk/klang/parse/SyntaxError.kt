package cz.j_jzk.klang.parse

/**
 * This execption means that there was a syntax error in the input.
 * 
 * The actual error should be handled by the
 * [onUnexpectedToken](cz.j_jzk.klang.lesana.LesanaBuilder.onUnexpectedToken)
 * callback in the lesana definition.
 */
class SyntaxError: Exception()
