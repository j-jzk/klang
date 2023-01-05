package cz.j_jzk.klang.prales.constants

import kotlin.test.Test
import cz.j_jzk.klang.prales.testLesana

class CharacterTest {
    @Test fun testBasic() {
        testLesana(
            character().getLesana(),
            mapOf(
                "'a'" to 'a',
                "' '" to ' ', 
                "'\\''" to '\'',
                "'\\n'" to '\n',
                "'✌'" to '✌', 
                "'\\\\'" to '\\',
                "'\\u1234'" to '\u1234',
            ),
            listOf("'''", "'\\'", "''", "'🚀'", "a"),
        )
    }
}
