package cz.j_jzk.klang.sele

import kotlin.test.Test
import kotlin.test.assertTrue

class SeleBuilderTest {
	@Test fun testBasicBuild() {
		val sele = sele {
			"int" to def(re("\\d+")) { (it[0]!! as String).toInt() }
			"sum" to def("int", re("\\+"), "int") { it[0]!! as Int + it[2]!! as Int }

			ignoreRegexes("\\s+")

			errorRecovering("sum")
			setTopNode("sum")
		}.getSele()

		// TODO: compare the output with an expected value when all the basic features are complete
		println(sele)
		// assertTrue(false)
	}
}
