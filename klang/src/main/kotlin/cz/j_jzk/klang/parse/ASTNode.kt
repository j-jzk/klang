package cz.j_jzk.klang.parse

import cz.j_jzk.klang.lex.Token
import cz.j_jzk.klang.parse.NodeID

// TODO
// for now, this is kinda just a placeholder
// sealed class ASTNode {
// 	data class Terminal<T>(val value: T): ASTNode() // integrate with token or not?
// 	data class Nonterminal<T>(val id: T, val children: List<ASTNode>): ASTNode()
// }
// TODO: be able to actually pass a value
data class ASTNode(val id: NodeID, val children: List<ASTNode>)
