package cz.j_jzk.klang.input

import java.io.FileInputStream

/**
 * An object for creating IdentifiableInputs from common sources, like files.
 *
 * However, this is not the only way to supply data to your lexer. You can use
 * any class which implements ListIterator, or use InputListIterator to convert
 * an InputStream to a ListIterator.
 */
object InputFactory {
    fun fromFile(path: String) = IdentifiableInput(path, InputListIterator(FileInputStream(path)))
	fun fromStdin() = IdentifiableInput("STDIN", InputListIterator(System.`in`))
	fun fromString(string: String, id: String) = IdentifiableInput(id, string.toList().listIterator())
}
