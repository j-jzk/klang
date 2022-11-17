package cz.j_jzk.klang.sele.tuple

import cz.j_jzk.klang.parse.NodeID

sealed class DataTuple<T1, T2, T3, T4> {
	data class Tuple1<T1>(val id1: T1): DataTuple<T1, Nothing, Nothing, Nothing>()
	data class Tuple2<T1, T2>(val id1: T1, val id2: T2): DataTuple<T1, T2, Nothing, Nothing>()
}

internal fun <T1, T2, T3, T4> dataTupleFromList(list: List<Any?>): DataTuple<T1, T2, T3, T4> =
	when (list.size) {
		1 -> DataTuple.Tuple1(list[0] as T1) as DataTuple<T1, T2, T3, T4>
		2 -> DataTuple.Tuple2(list[0] as T1, list[1] as T2) as DataTuple<T1, T2, T3, T4>
		else -> throw IllegalArgumentException("List too long to convert to a tuple")
	}
