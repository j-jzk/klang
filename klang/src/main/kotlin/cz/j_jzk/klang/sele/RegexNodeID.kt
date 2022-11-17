package cz.j_jzk.klang.sele

import cz.j_jzk.klang.parse.NodeID

/**
 * A node ID representing a regex node.
 * Created using `re()` in the sele definition.
 */
data class RegexNodeID(val regex: String): NodeID<String>()
