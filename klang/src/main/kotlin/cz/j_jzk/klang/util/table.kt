package cz.j_jzk.klang.util

import com.google.common.collect.Table

internal operator fun <R, C, V> Table<R, C, V>.set(row: R, col: C, value: V) =
	put(row, col, value)
