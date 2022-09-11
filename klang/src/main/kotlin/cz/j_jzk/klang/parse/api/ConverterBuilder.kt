package cz.j_jzk.klang.parse.api

/**
 * A class for declaring conversions from lexer tokens to AST node values.
 * You probably don't want to use this class directly, but instead create
 * a `conversions` block inside `lexer()`
 */
class ConverterBuilder {
	private val conversions = mutableMapOf<Any, (String) -> Any>()

	/** Maps one token type to a conversion function */
	infix fun Any.to(conversion: (String) -> Any) {
		conversions[this] = conversion
	}

	/** Maps multiple token types to a single conversion function */
	infix fun List<Any>.to(conversion: (String) -> Any) {
		for (id in this)
			id to conversion
	}

	/** Returns the map of the conversions defined */
	internal fun getConversions(): Map<Any, (String) -> Any> = conversions
}
