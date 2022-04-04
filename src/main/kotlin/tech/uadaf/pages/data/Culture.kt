package tech.uadaf.pages.data

import dawnbreaker.data.raw.Culture
import kotlinx.html.DIV
import kotlinx.html.li
import kotlinx.html.ul
import tech.uadaf.pages.bool
import tech.uadaf.pages.field
import tech.uadaf.pages.str

fun DIV.culture(x: Culture) = dataPage(x) {
    field("Exonym: ") { str(x.exonym) }
    field("Endonym: ") { str(x.endonym) }
    field("Font script: ") { str(x.fontscript) }
    field("Bold allowed? ") { bool(x.boldallowed) }
    field("Released? ") { bool(x.released) }
    field("UI Labels:") {
        ul {
            x.uilabels.forEach { (a, b) ->
                li { +"$a: $b" }
            }
        }
    }
}