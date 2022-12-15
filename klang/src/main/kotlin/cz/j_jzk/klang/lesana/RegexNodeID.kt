package cz.j_jzk.klang.lesana

import cz.j_jzk.klang.parse.NodeID

/**
 * A node ID representing a regex node.
 * Created using `re()` in the lesana definition.
 */
data class RegexNodeID(val regex: String): NodeID<String>()
