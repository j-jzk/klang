package cz.j_jzk.klang.parse.algo

import cz.j_jzk.klang.parse.ASTNode

sealed class ASTData {
    data class Terminal(val value: String): ASTData()
    data class Nonterminal(val children: List<ASTNode<ASTData>>): ASTData()
}
