package cz.j_jzk.klang.parse.algo

// TODO: typealias this to int to save memory?
data class State(val id: Int)

object StateFactory {
	private var i = 0
	fun new() = State(i++)
}
