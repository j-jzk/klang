package cz.j_jzk.klang.parse.algo

/** Represents a state of the parser; intended for internal use */
// Maybe we could store information about the error-recovery states in a list and
// typealias this to int to save memory?
data class State(val id: Int, val errorRecovering: Boolean)

internal class StateFactory {
	private var i = 0
	fun new(errorRecovering: Boolean = false) = State(i++, errorRecovering)
}
