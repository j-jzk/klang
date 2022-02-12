package cz.j_jzk.klang.parse.api

class ConverterBuilder<I, D> {
	private val conversions = mutableMapOf<I, (String) -> D>()

	infix fun I.to(conversion: (String) -> D) {
		conversions[this] = conversion
	}

	infix fun List<I>.to(conversion: (String) -> D) {
		for (id in this)
			id to conversion
	}

	fun getConversions(): Map<I, (String) -> D> = conversions
}
