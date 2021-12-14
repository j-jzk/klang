package cz.j_jzk.klang.testutils

import cz.j_jzk.klang.lex.re.compileRegex

fun re(str: String) = compileRegex(str).fa
fun iter(str: String) = str.toList().listIterator()
