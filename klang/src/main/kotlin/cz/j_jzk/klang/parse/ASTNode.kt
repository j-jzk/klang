package cz.j_jzk.klang.parse

/**
 * Represents a node of the AST. For now, this is just a placeholder.
 */
// TODO: be able to actually pass a value
data class ASTNode(val id: NodeID, val children: List<ASTNode>)
