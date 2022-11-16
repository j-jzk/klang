package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.re.CompiledRegex

/** Represents a definition of a node of the AST (= one statement in the formal grammar) */
data class NodeDef(
	val elements: List<NodeID>,
	val reduction: (List<ASTNode>) -> ASTNode,
	val lexerIgnores: Set<CompiledRegex> = emptySet(),
)
