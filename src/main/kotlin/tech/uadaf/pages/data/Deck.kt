package tech.uadaf.pages.data

import dawnbreaker.data.raw.Deck
import kotlinx.html.DIV
import kotlinx.html.em
import kotlinx.html.li
import kotlinx.html.ul
import tech.uadaf.pages.*

fun DIV.deck(x: Deck) = dataPage(x) {
    field("Label: ") { localizations(x.label) }
    field("Description: ") { localizations(x.description) }
    field("Draw Messages: ") {
        if(x.drawmessages.isEmpty()) {
            em { +"None" }
        } else {
            ul {
                x.drawmessages.forEach { (id, msg) ->
                    li {
                        elementRef(id)
                        localizations(msg)
                    }
                }
            }
        }
    }
    field("Cards: ") { elementList(x.spec.asSequence().map { it to 1 }.toMap()) }
    field("Default card: ") { if(x.defaultcard.isNotBlank()) elementRef(x.defaultcard, "1") else em { +"None" } }
    field("Reset on Exhaustion? ") { bool(x.resetonexhaustion)}
    field("For legacy family: ") { if(x.forlegacyfamily.isNotBlank()) +x.forlegacyfamily else em { +"None" } }
    field("Comments: ") { str(x.comments) }
}