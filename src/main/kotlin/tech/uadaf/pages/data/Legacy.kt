package tech.uadaf.pages.data

import dawnbreaker.data.raw.Legacy
import kotlinx.html.DIV
import kotlinx.html.a
import tech.uadaf.csdata.endingPage
import tech.uadaf.csdata.verbPage
import tech.uadaf.pages.*

fun DIV.legacy(x: Legacy) = dataPage(x) {
    field("Label: ") { localizations(x.label) }
    field("Start Description: ") { localizations(x.startdescription) }
    field("Description: ") { localizations(x.description) }
    field("From Ending: ") {
        if(x.fromending.isBlank()) {
            +"None"
        } else {
            endingRef(x.fromending)
        }
    }
    field("Excludes on Ending: ") {
        if(x.excludesOnEnding.isEmpty()) {
            +"None"
        } else {
            x.excludesOnEnding.forEach {
                endingRef(it)
            }
        }
    }
    field("Available Without Ending Match? ") { bool(x.availableWithoutEndingMatch) }
    field("Starting Verb: ") { verbRef(x.startingverbid) }
    field("Starting Elements: ") { elementList(x.effects) }
    field("Comments: ") { str(x.comments) }
}