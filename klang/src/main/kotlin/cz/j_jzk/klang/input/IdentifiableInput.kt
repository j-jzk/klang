package cz.j_jzk.klang.input

/**
 * This data class is used for identifying an input with a textual ID; this is
 * useful for logging syntax errors.
 *
 * Several factory functions for the usual types of input are in the [InputFactory]
 * object.
 */
data class IdentifiableInput(
	val id: String,
	val input: ListIterator<Char>,
)
