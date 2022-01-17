package cz.j_jzk.klang.parse.algo

object StateFactory {
	private var i = 0
	fun new() = State(i++)
}

class ParserDFABuilder {

}