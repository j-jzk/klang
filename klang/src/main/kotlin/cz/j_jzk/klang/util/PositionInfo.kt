package cz.j_jzk.klang.util

/**
 * Stores information about a character position in an input, typically the
 * position of an [ASTNode](cz.j_jzk.klang.parse.ASTNode).
 * 
 * @property inputId The ID of the input (filename, STDIN etc.)
 * @property character The character position
 */
data class PositionInfo(
	val inputId: String,
	val character: Int,
)
