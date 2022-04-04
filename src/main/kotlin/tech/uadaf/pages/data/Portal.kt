package tech.uadaf.pages.data

import dawnbreaker.data.raw.Portal
import kotlinx.html.DIV
import kotlinx.html.li
import kotlinx.html.ul
import tech.uadaf.pages.*

/*
@Serializable
data class Portal(
    @Required override var id: String = "",
    var icon: String = "",
    var label: String = "",
    var description: String = "",
    @SerialName("otherworldid")
    var otherworldId: String = "",
    @SerialName("egressid")
    var egressId: String = "",
    var consequences: MutableList<Consequence> = mutableListOf(),
    @SerialName("consequences\$append")
    var consequences_append: MutableList<Consequence> = mutableListOf(),
    @SerialName("consequences\$prepend")
    var consequences_prepend: MutableList<Consequence> = mutableListOf(),
    @SerialName("consequences\$remove")
    var consequences_remove: MutableList<String> = mutableListOf(),
) : Data
 */

fun DIV.portal(x: Portal) = dataPage(x) {
    field("Label: ") { str(x.label) }
    field("Description: ") { str(x.description) }
    field("Otherworld: ") { str(x.otherworldId) }
    field("Egress: ") { str(x.egressId) }
    field("Consequences: ") {
        ul {
            x.consequences.forEach {
                li {
                    subfield("Path: ") { str(it.toPath) }
                    subfield("Deck effects: ") { deckList(it.deckeffects.mapValues { it.value.toString() }) }
                }
            }
        }
    }
}