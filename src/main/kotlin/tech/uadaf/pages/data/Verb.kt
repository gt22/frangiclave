package tech.uadaf.pages.data

import dawnbreaker.data.raw.Verb
import dawnbreaker.locale.data.VerbLocale
import kotlinx.html.DIV
import tech.uadaf.pages.field
import tech.uadaf.pages.localizations
import tech.uadaf.pages.slots
import tech.uadaf.pages.str

fun DIV.verb(x: Verb) = dataPage(x) {
    field("Label: ") { localizations(x) { r: VerbLocale -> r.label } }
    field("Description: ") { localizations(x) { r: VerbLocale -> r.description } }
    field("Comments: ") { str(x.comments) }
    field("Slot: ") { x.slot?.let { slots(it) } }
}