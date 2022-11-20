package cz.j_jzk.klang.sele.tuple

import cz.j_jzk.klang.parse.NodeID

sealed class DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, > {
	data class Tuple1<T1, >(var id1: T1, ): DataTuple<T1, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple2<T1, T2, >(var id1: T1, var id2: T2, ): DataTuple<T1, T2, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple3<T1, T2, T3, >(var id1: T1, var id2: T2, var id3: T3, ): DataTuple<T1, T2, T3, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple4<T1, T2, T3, T4, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, ): DataTuple<T1, T2, T3, T4, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple5<T1, T2, T3, T4, T5, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, ): DataTuple<T1, T2, T3, T4, T5, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple6<T1, T2, T3, T4, T5, T6, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, ): DataTuple<T1, T2, T3, T4, T5, T6, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple7<T1, T2, T3, T4, T5, T6, T7, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Nothing, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, var id16: T16, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Nothing, Nothing, Nothing, Nothing, >()
//	data class Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, var id16: T16, var id17: T17, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, Nothing, Nothing, Nothing, >()
//	data class Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, var id16: T16, var id17: T17, var id18: T18, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, Nothing, Nothing, >()
//	data class Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, var id16: T16, var id17: T17, var id18: T18, var id19: T19, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, Nothing, >()
//	data class Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >(var id1: T1, var id2: T2, var id3: T3, var id4: T4, var id5: T5, var id6: T6, var id7: T7, var id8: T8, var id9: T9, var id10: T10, var id11: T11, var id12: T12, var id13: T13, var id14: T14, var id15: T15, var id16: T16, var id17: T17, var id18: T18, var id19: T19, var id20: T20, ): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >()	
}

internal fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, > dataTupleFromList(list: List<Any?>): DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, > =
	when (list.size) {
		1 -> DataTuple.Tuple1(list[0] as T1, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		2 -> DataTuple.Tuple2(list[0] as T1, list[1] as T2, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		3 -> DataTuple.Tuple3(list[0] as T1, list[1] as T2, list[2] as T3, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		4 -> DataTuple.Tuple4(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		5 -> DataTuple.Tuple5(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		6 -> DataTuple.Tuple6(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		7 -> DataTuple.Tuple7(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		8 -> DataTuple.Tuple8(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		9 -> DataTuple.Tuple9(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		10 -> DataTuple.Tuple10(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		11 -> DataTuple.Tuple11(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		12 -> DataTuple.Tuple12(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		13 -> DataTuple.Tuple13(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		14 -> DataTuple.Tuple14(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		15 -> DataTuple.Tuple15(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		16 -> DataTuple.Tuple16(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, list[15] as T16, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		17 -> DataTuple.Tuple17(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, list[15] as T16, list[16] as T17, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		18 -> DataTuple.Tuple18(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, list[15] as T16, list[16] as T17, list[17] as T18, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		19 -> DataTuple.Tuple19(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, list[15] as T16, list[16] as T17, list[17] as T18, list[18] as T19, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
//		20 -> DataTuple.Tuple20(list[0] as T1, list[1] as T2, list[2] as T3, list[3] as T4, list[4] as T5, list[5] as T6, list[6] as T7, list[7] as T8, list[8] as T9, list[9] as T10, list[10] as T11, list[11] as T12, list[12] as T13, list[13] as T14, list[14] as T15, list[15] as T16, list[16] as T17, list[17] as T18, list[18] as T19, list[19] as T20, ) as DataTuple<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, >
		else -> throw IllegalArgumentException("List too long to convert to a tuple")
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
