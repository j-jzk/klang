package cz.j_jzk.klang.parse

/**
 * This class includes information about an unexpected token error -
 * what tokens were expected and what did we get
 * 
 * @property expectedIDs What did we expect
 * @property got What did we get - the unexpected token (also contains information
 *  about its position = the position of the error)
 */
data class UnexpectedTokenError(
    val got: ASTNode,
    val expectedIDs: Collection<NodeID<*>>,
): Exception() {
    override fun toString(): String {
        if (got.id == UnexpectedCharacter && got is ASTNode.Data) {
            return "Unexpected character '${got.data}'"
        } else {
            val sb = StringBuilder()
            sb.append("Unexpected token ${got.id}, expected one of: [")
            sb.append(expectedIDs.filter { it.show }.joinToString(", "))
            sb.append("].")
            return sb.toString()
        }
    }
}
