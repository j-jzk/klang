package cz.j_jzk.klang.prales.useful;

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana

class RawCharacterTest {
    @Test fun testBasic() {
        println(rawCharacter().toString())
        testLesana(
            rawCharacter().getLesana(),
            mapOf(
                // " " to ' ',
                // "a" to 'a',
                // "\\n" to '\n',
                // "." to '.',
                // "'" to '\'',
                // "\\\\" to '\\',
                // "😂" to '😂',
                // "★" to '★',
                "\\u2605" to '\u2605',
            ),
            listOf(

            ),
        )
    }
}
