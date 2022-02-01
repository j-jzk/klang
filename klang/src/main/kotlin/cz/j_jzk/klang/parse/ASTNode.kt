package cz.j_jzk.klang.parse

/**
 * Represents a node of the AST.
 */
// TODO: how to store EOF?
data class ASTNode<N>(val id: NodeID, val data: N)
