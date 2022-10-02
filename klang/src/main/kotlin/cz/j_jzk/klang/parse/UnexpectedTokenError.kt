package cz.j_jzk.klang.parse

/**
 * This class includes information about an unexpected token error -
 * what tokens were expected and what did we get
 */
data class UnexpectedTokenError(
    val got: ASTNode,
    val expectedIDs: Collection<Any>,
): Exception() {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Unexpected token ${got.id}, expected one of: [")
        sb.append(expectedIDs.joinToString(", "))
        sb.append("].")
        return sb.toString()
    }
}
