package cz.j_jzk.klang.parse.algo

/** Represents a state of the parser; intended for internal use */
// TODO: typealias this to int to save memory?
data class State(val id: Int)

internal object StateFactory {
	private var i = 0
	fun new() = State(i++)
}
