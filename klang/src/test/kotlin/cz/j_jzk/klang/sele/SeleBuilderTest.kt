package cz.j_jzk.klang.sele

import kotlin.test.Test

class SeleBuilderTest {
	@Test fun testBasicBuild() {
		sele {
			"int" to def(re("\\d+")) { (it[0]!! as String).toInt() }
			"sum" to def("int", re("\\+"), "int") { it[0]!! as Int + it[2]!! as Int }

			errorRecovering("sum")
			setTopNode("sum")
		}.getSele()

		// TODO: compare the output with an expected value when all the basic features are complete
		// println(sele)
	}
}
