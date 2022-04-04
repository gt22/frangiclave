package tech.uadaf.pages.data

import dawnbreaker.data.raw.Portal
import kotlinx.html.DIV
import kotlinx.html.li
import kotlinx.html.ul
import tech.uadaf.pages.deckList
import tech.uadaf.pages.field
import tech.uadaf.pages.str
import tech.uadaf.pages.subfield

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