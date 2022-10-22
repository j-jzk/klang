package cz.j_jzk.klang.sele

import kotlin.test.Test

class SeleBuilderTest {
	@Test fun testBasicBuild() {
		sele {
			"sum" to def("int", "plus", "int") { it[0]!! as Int + it[2]!! as Int }

			errorRecovering("sum")
		}
	}
}
