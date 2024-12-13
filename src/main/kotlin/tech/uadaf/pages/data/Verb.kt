package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Verb
import dawnbreaker.locale.data.VerbLocale
import kotlinx.html.DIV
import tech.uadaf.pages.*

fun DIV.verb(x: Verb) = dataPage(x, x.textContent()) {
    field("Label: ") { localizations(x) { r: VerbLocale -> r.label } }
    field("Description: ") { localizations(x) { r: VerbLocale -> r.description } }
    field("Comments: ") { str(x.comments) }
    field("Aspects: ") { elementList(x.aspects) }
    field("Slots: ") { slots(*x.slots.toTypedArray()) }
}

fun Verb.textContent() = "$label\n$description"