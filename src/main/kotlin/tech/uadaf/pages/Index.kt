package tech.uadaf.pages

import kotlinx.html.*
import tech.uadaf.config
import tech.uadaf.csdata.frangiclave
import tech.uadaf.theme


fun HTML.index() = basePage(head = {
    theme.title.let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    theme.description.let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    frangiclave(theme.logo).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}) {
    img(theme.logo, frangiclave(theme.logo)) { id = "logo" }
    p {
        id = "tagline"
        +theme.description
    }
    p {
        +"The ${theme.title} is an open-source repository for information about the contents of the game ${config.game}, as extracted from the game's files. Here you can browse the decks, elements, legacies, recipes and verbs included in the game."
        br { }
        strong {
            +"Reuse is permitted if it follows the rules in the "
            a(href = "https://weatherfactory.biz/sixth-history-community-licence/") { +"Sixth History License" }
        }
    }
    p {
        +"Data taken from: ${config.version}"
    }
    div {
        p("index-foot") {
            strong { +"Source: " }
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