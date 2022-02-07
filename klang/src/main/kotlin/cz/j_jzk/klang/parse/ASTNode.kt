package cz.j_jzk.klang.parse

/**
 * Represents a node of the AST.
 */
data class ASTNode<out N>(val id: NodeID, val data: N)
