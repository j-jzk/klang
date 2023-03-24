package cz.j_jzk.klang.lesana

import cz.j_jzk.klang.parse.NodeID

/**
 * A node ID representing a regex node.
 * Created using [`re()`](LesanaBuilder.re) in the lesana builder.
 */
class RegexNodeID(val regex: String, show: Boolean = true): NodeID<String>("regex($regex)", show) {
    override fun equals(other: Any?) =
        if (other is RegexNodeID)
            other.regex == this.regex
        else
            false

    override fun hashCode() = regex.hashCode()
}
