package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.re.CompiledRegex

/** Represents a definition of a node of the AST (= one statement in the formal grammar) */
data class NodeDef(
	val elements: List<NodeID<Any?>>,
	val reduction: (List<ASTNode>) -> ASTNode,
	val lexerIgnores: Set<CompiledRegex> = emptySet(),
	/**
	 * The regexes to be ignored when the end of the item is reached.
	 *
	 * The reason for this is that when we reach the end of the item, the next
	 * node, which correctly appears in the sigma set, might be behind an
	 * ignored character.
	 *
	 * Thus, when we reach the end of this definition, we also need to ignore
	 * the regexes ignored in the lesana we reduce into.
	 */
	val ignoreAfter: Set<CompiledRegex> = emptySet(),
)
