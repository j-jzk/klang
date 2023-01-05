package cz.j_jzk.klang.prales.constants

import cz.j_jzk.klang.prales.testLesana
import kotlin.test.Test

class StringTest {
    @Test fun testBasic() {
        testLesana(
            string().getLesana(),
            mapOf(
                "\"hello\"" to "hello",
                "\"\"" to "",
                """"hello \" \n \\ 🚀 \u1234"""" to "hello \" \n \\ \uD83D\uDE80 \u1234",
            ),
            listOf(
                "\"\"\"",
                "\"\\\"",
                "\"\"\"",
                "\"\\ \"",
            ),
        )
    }

    @Test fun testCustomQuotes() {
        testLesana(
            string("'").getLesana(),
            mapOf(
                "'hello'" to "hello",
                "''" to "",
                "'\"'" to "\"",
                """'hello \' \n \\ 🚀 \u1234'""" to "hello ' \n \\ \uD83D\uDE80 \u1234",
            ),
            listOf(
                "\"hello\"",
                "'''",
                "'\\'",
            ),
        )
    }
}
