package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Deck
import dawnbreaker.locale.data.DeckLocale
import kotlinx.html.DIV
import kotlinx.html.em
import kotlinx.html.li
import kotlinx.html.ul
import tech.uadaf.content
import tech.uadaf.pages.*

fun DIV.deck(x: Deck) = dataPage(x) {
    field("Label: ") { localizations(x) { s: DeckLocale -> s.label }}
    field("Description: ") { localizations(x) { s: DeckLocale -> s.description } }
    field("Draw Messages: ") {
        if(x.drawmessages.isEmpty()) {
            em { +"None" }
        } else {
            ul {
                x.drawmessages.forEach { (id, _) ->
                    li {
                        elementRef(id)
                        localizations(x) { s: DeckLocale -> s.drawmessages[id] ?: "None" }
                    }
                }
            }
        }
    }
    field("Cards: ") { elementList(x.spec.groupingBy { it }.eachCount()) }
    field("Default card: ") { if(x.defaultcard.isNotBlank()) elementRef(x.defaultcard, "1") else em { +"None" } }
    field("Reset on Exhaustion? ") { bool(x.resetonexhaustion)}
    field("Drawn in: ") { recipeList(
        content.recipes.filter { x.id in it.deckeffects }.associate { it.id to it.deckeffects[x.id]!! } + content.recipes.filter { it.internaldeck == x }.associate { it.id to x.draws.toString() })
    }
    field("For legacy family: ") { if(x.forlegacyfamily.isNotBlank()) +x.forlegacyfamily else em { +"None" } }
    field("Comments: ") { str(x.comments) }
}