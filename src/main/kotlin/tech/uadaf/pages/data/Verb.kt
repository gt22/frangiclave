package tech.uadaf.pages.data

import dawnbreaker.data.raw.Verb
import kotlinx.html.DIV
import tech.uadaf.pages.*

fun DIV.verb(x: Verb) = dataPage(x) {
    field("Label: ") { localizations(x.label) }
    field("Description: ") { localizations(x.description) }
    field("Comments: ") { str(x.comments) }
    field("Slot: ") { x.slot?.let { slots(it) } }
}