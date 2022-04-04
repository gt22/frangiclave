package tech.uadaf.pages.data

import dawnbreaker.data.raw.Ending
import kotlinx.html.DIV
import tech.uadaf.pages.field
import tech.uadaf.pages.localizations
import tech.uadaf.pages.str

fun DIV.ending(x: Ending) = dataPage(x) {
    field("Title: ") { localizations(x.label) }
    field("Description: ") { localizations(x.description) }
    field("Ending flavor: ") { str(x.flavour) }
    field("Animation: ") { str(x.anim) }
    field("Achievement: ") { str(x.achievement) }
    field("Comments: ") { str(x.comments) }
}