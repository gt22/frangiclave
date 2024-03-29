package tech.uadaf.pages

import kotlinx.html.*
import tech.uadaf.config
import tech.uadaf.csdata.frangiclave

private const val description =
    "Welcome, friend, to the Frangiclave Compendium, the premier source of forbidden knowledge for the discerning occultist."

fun HTML.index() = basePage(head = {
    "Frangiclave Compendium".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    description.let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    frangiclave("toolknockf").let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}) {
    img("frangiclave", frangiclave("toolknockf")) { id = "logo" }
    p {
        id = "tagline"
        +description
    }
    p {
        +"The Frangiclave Compendium (Curia edition) is an open-source repository for information about the contents of the game Book of Hours beta version Curia, as extracted from the game's files. Here you can browse the decks, elements, legacies, recipes and verbs included in the game."
    }
    p {
        +"Data taken from: ${config.version}"
    }
    div {
        p("index-foot") {
            strong { +"Source:" }
            a("https://github.com/gt22/frangiclave") { +"gt22/frangiclave" }
        }
        p("index-foot") {
            +"Hosted by Frgm"
            br {}
        }
        p("index-foot") {
            +"Based on the original "
            a("https://github.com/frangiclave/frangiclave-compendium") { +"Frangiclave Compendium" }
            +" by Lyrositor"
        }
        p {
            id = "donate"
            +"If you'd like to donate, feel free to "
            a("https://ko-fi.com/lyrositor") { +"Buy Lyro a coffee" }
            +"."
        }
    }
}