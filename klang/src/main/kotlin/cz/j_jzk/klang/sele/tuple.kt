package cz.j_jzk.klang.sele.tuple

import cz.j_jzk.klang.parse.NodeID

sealed class DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, > {
	data class Tuple1<T1, >(var id1: T1, ): DataTuple<T1, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple2<T1, T2, >(var id1: T1, var id2: T2, ): DataTuple<T1, T2, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple3<T1, T2, T3, >(var id1: T1, var id2: T2, var id3: T3, ): DataTuple<T1, T2, T3, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple4<T1, T2, T3, T4, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, ): DataTuple<T1, T2, T3, T4, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple5<T1, T2, T3, T4, T5, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, ): DataTuple<T1, T2, T3, T4, T5, Nothing, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple6<T1, T2, T3, T4, T5, T6, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, ): DataTuple<T1, T2, T3, T4, T5, T6, Nothing, Nothing, Nothing, Nothing, >()
	data class Tuple7<T1, T2, T3, T4, T5, T6, T7, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, Nothing, Nothing, Nothing, >()
	data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, Nothing, Nothing, >()
	data class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, Nothing, >()
	data class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >()
}

internal fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, > dataTupleFromList(list: List<Any?>): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, > =
	when (list.size) {
		1 -> DataTuple.Tuple1(list[0] as T1, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		2 -> DataTuple.Tuple2(list[0] as T1, list[1] as T2, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		3 -> DataTuple.Tuple3(list[0] as T1, list[1] as T2, list[2] as T3, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		4 -> DataTuple.Tuple4(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		5 -> DataTuple.Tuple5(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		6 -> DataTuple.Tuple6(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		7 -> DataTuple.Tuple7(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		8 -> DataTuple.Tuple8(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		9 -> DataTuple.Tuple9(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		10 -> DataTuple.Tuple10(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >
		else -> throw IllegalArgumentException("List too long to convert to a tuple. This should never happen, file a bug report.")
}

/*
SCRIPT:

basetparams = "<"
for i in range(1, 21):
	basetparams += f"T{i}, "
basetparams += ">"

anys = ""
for i in range(1, 21):
	anys += "Any?, "

tupledefs = []
whenrows = []
defuns = []

for i in range(1, 21):
	tupledef = f"data class Tuple{i}<"
	for j in range(1, i+1):
		tupledef += f"T{j}, "
	tupledef += ">("
	for j in range(1, i+1):
		tupledef += f"var id{j}: T{j}, "

	tupledef += "): DataTuple<"
	for j in range(1, i+1):
		tupledef += f"T{j}, "
	for j in range(i+1, 21):
		tupledef += "Nothing, "
	tupledef += ">()"
	tupledefs.append(tupledef)
	
	whenrow = f"{i} -> DataTuple.Tuple{i}("
	for j in range(i):
		whenrow += f"list[{j}] as T{j+1}, "
	whenrow += f") as DataTuple{basetparams}"
	whenrows.append(whenrow)


	defun = "fun <"
	for j in range(1, i+1):
		defun += f"T{j}, "
	defun += "R> def("
	defun += ", ".join([f"id{j}: NodeID<T{j}>" for j in range(1, i+1)])
	defun += f", reduction: (DataTuple.Tuple{i}<"
	defun += ", ".join([f"T{j}" for j in range(1, i+1)])
	defun += ">) -> R) =\n\tIntermediateNodeDefinition<"
	for j in range(1, i+1):
		defun += f"T{j}, "
	for j in range(i+1, 21):
		defun += "Nothing, "
	defun += f"DataTuple.Tuple{i}<"
	defun += ", ".join([f"T{j}" for j in range(1, i+1)])
	defun += f">, R>(listOf("
	defun += ", ".join([f"id{j}" for j in range(1, i+1)])
	defun += "), reduction)"
	defuns.append(defun)

print(anys)
print("\n\n")
print(basetparams)
print("\n\n")
print("\n".join(tupledefs))
print("\n\n")
print("\n".join(whenrows))
print("\n\n")
print("\n".join(defuns))


*/
