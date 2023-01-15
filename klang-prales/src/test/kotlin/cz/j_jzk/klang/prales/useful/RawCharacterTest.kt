package cz.j_jzk.klang.prales.useful;

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana

class RawCharacterTest {
    @Test fun testBasic() {
        testLesana(
            rawCharacter().getLesana(),
            mapOf(
                 " " to ' ',
                 "a" to 'a',
                 "\\n" to '\n',
                 "." to '.',
                 "'" to '\'',
                 "\\\\" to '\\',
                 "★" to '★',
                "\\u2605" to '★',
            ),
            listOf(
                "😂",  // a four-byte character, does not fit into a single char
                "aa",
                "\\",
                "",
            ),
        )
    }

    @Test fun testSpecialCharacters() {
        testLesana(
                rawCharacter("\\\\123").getLesana(),
                mapOf(
                    " " to ' ',
                    "a" to 'a',
                    "\\n" to '\n',
                    "\\1" to '1',
                    "\\2" to '2',
                    "\\\\" to '\\',
                    "★" to '★',
                    "\\u2605" to '★',
                ),
                listOf(
                    "😂",  // a four-byte character, does not fit into a single char
                    "aa",
                    "\\",
                    "",
                    "1",
                ),
        )
    }
}
