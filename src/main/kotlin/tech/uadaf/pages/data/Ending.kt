package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Ending
import dawnbreaker.locale.data.EndingLocale
import kotlinx.html.DIV
import tech.uadaf.pages.field
import tech.uadaf.pages.localizations
import tech.uadaf.pages.str

fun DIV.ending(x: Ending) = dataPage(x, x.textContent()) {
    field("Title: ") { localizations(x) { e: EndingLocale -> e.label } }
    field("Description: ") { localizations(x) { e: EndingLocale -> e.description } }
    field("Ending flavor: ") { str(x.flavour) }
    field("Animation: ") { str(x.anim) }
    field("Achievement: ") { str(x.achievement) }
    field("Comments: ") { str(x.comments) }
}

fun Ending.textContent() = "$label\n$description"