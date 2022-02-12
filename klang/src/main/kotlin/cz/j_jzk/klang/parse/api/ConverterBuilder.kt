package cz.j_jzk.klang.parse.api

/**
 * A class for declaring conversions from lexer tokens to AST node values.
 * You probably don't want to use this class directly, but instead create
 * a `conversions` block inside `lexer()`
 */
class ConverterBuilder<I, D> {
	private val conversions = mutableMapOf<I, (String) -> D>()

	/** Maps one token type to a conversion function */
	infix fun I.to(conversion: (String) -> D) {
		conversions[this] = conversion
	}

	/** Maps multiple token types to a single conversion function */
	infix fun List<I>.to(conversion: (String) -> D) {
		for (id in this)
			id to conversion
	}

	/** Returns the map of the conversions defined */
	internal fun getConversions(): Map<I, (String) -> D> = conversions
}
