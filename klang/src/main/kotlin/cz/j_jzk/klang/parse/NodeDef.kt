package cz.j_jzk.klang.parse

/** Represents a definition of a node of the AST (= one statement in the formal grammar) */
data class NodeDef(
	val elements: List<NodeID>,
	val reduction: (List<ASTNode>) -> ASTNode,
)
