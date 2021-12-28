package cz.j_jzk.klang.util.list

public fun <T> MutableList<T>.pop(): T {
	val value = this.last()
	this.removeAt(this.size - 1)
	return value
}
