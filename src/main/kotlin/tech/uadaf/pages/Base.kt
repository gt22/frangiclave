package tech.uadaf.pages

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.Element
import dawnbreaker.data.raw.Source
import dawnbreaker.locale.data.ElementLocale
import kotlinx.html.*
import tech.uadaf.baseUrl
import tech.uadaf.content
import tech.uadaf.csdata.*
import tech.uadaf.theme


fun HTML.basePage(
    activeItem: String = "",
    title: String = theme.title,
    keywords: String = "",
    sidebar: Boolean = true,
    head: HEAD.() -> Unit = {},
    content: DIV.() -> Unit
) {
    head {
        meta("robots", "noindex, nofollow")
        link(rel = "stylesheet", href = "$baseUrl/styles.css", type = "text/css") {
            id = "styles"
        }
        style(type = "text/css") {
            id = "darktheme"
            media = "prefers-color-scheme: dark"
            unsafe { +darktheme }
        }
        inlinedThemeScript()
        link(rel = "stylesheet", href = "https://fonts.googleapis.com/css?family=Forum|Lato")
        link(rel = "icon", type = "image/png", href = frangiclave(theme.icon))
        script {
            defer = true
            src = "$baseUrl/static/script/sidebar.js"
        }
        script {
            defer = true
            src = "$baseUrl/static/script/clipboard.min.js"
        }
        script {
            src = "$baseUrl/static/script/theme.js"
        }
        title(title)
        head()
    }
    body {
        header(keywords)
        div {
            id = "container"
            sidebar(activeItem, sidebar)
            div {
                id = "content"
                content()
            }
        }
        footer()
    }
}

fun FlowContent.lightswitchRef(id: String, text: String, icon: String) = a("#", classes = "lightswitch-ref ref") {
    this.id = id
    img("", icon, classes = "lightswitch-ref ref-icon") {}
    span("lightswitch-ref ref-text ref-id") { +text }
}

fun BODY.header(keywords: String) = header {
    a("$baseUrl/") {
        img(theme.icon, frangiclave(theme.icon))
        h1 { +theme.title }
    }
    div {
        id = "switchbox"
        div {
            id = "lightswitch"
            lightswitchRef("lightswitch-auto", "Auto", frangiclave("colours.liminal"))
            lightswitchRef("lightswitch-light", "Light", frangiclave("colours.splendid"))
            lightswitchRef("lightswitch-dark", "Dark", frangiclave("colours.honeyed"))
        }
    }
    form {
        id = "search-box"
        action = "$baseUrl/search"
        method = FormMethod.get
        input(InputType.search) {
            title = "Search"
            name = "keywords"
            id = "search-text"
            value = keywords
        }
        input(InputType.submit) {
            value = "Search"
            id = "search-submit"
        }
    }
}

fun DIV.sidebar(activeItem: String, sidebar: Boolean) = div {
    id = "sidebar"
    if (sidebar) {
        div {
            id = "sections"
            sidebarSection("Achievements", "achievement", activeItem) { achievements }
            sidebarSection("Decks", "deck", activeItem) { decks }
            sidebarSection("Elements", "element", activeItem) { elements }
            sidebarSection("Endings", "ending", activeItem) { endings }
            sidebarSection("Legacies", "legacy", activeItem) { legacies }
            sidebarSection("Recipes", "recipe", activeItem) { recipes }
            sidebarSection("Rooms", "room", activeItem) { rooms }
            sidebarSection("Verbs", "verb", activeItem) { verbs }
            sidebarSection("Cultures", "culture", activeItem) { cultures }
            sidebarSection("Dicta", "dicta", activeItem) { dicta }
            sidebarSection("Portals", "portal", activeItem) { portals }
        }
    }
}

inline fun DIV.sidebarSection(title: String, type: String, activeItem: String, getter: Source.() -> List<Data>) {
    val data = content.sources.map { (name, s) ->
        name.removePrefix("${title.lowercase()}/") to s.getter().map { it.id }
    }.toList()
    if (data.any { (_, d) -> d.isNotEmpty() }) {
        sidebarSection(title, type, activeItem, data)
    }
}

fun DIV.sidebarSection(title: String, type: String, activeItem: String, content: List<Pair<String, List<String>>>) {
    div("section-title") { +title }
    val (activeType, activeId) = if (activeItem.contains(':')) activeItem.split(":", limit = 2) else listOf("", "")
    div("section-list ${if (activeType == type) "section-list-opened" else ""}") {
        content.asSequence()
            .filter { it.second.isNotEmpty() }
            .sortedBy { it.first.lowercase() }
            .forEach { (file, items) ->
                div("section-file") {
                    div("section-file-title") { +file }
                    items.asSequence().sorted().forEach { item ->
                        a(page(type, item), classes = "section-item") {
                            if (item == activeId) {
                                id = "section-item-active"
                            }
                            +item
                        }
                    }
                }
            }
    }
}

fun BODY.footer() = footer {
    +"Cultist Simulator and Book of Hours is the sole property of Weather Factory. All rights reserved."
    br { }
    +"All game content on this website, including images and text, is used with permission."
    br { }
    +"Reuse is permitted if it follows the rules in the "
    a(href = "https://weatherfactory.biz/sixth-history-community-licence/", target = "_blank") { +"Sixth History License" }
}

fun HEAD.inlinedThemeScript() = script {
    unsafe {
        +("function updateTheme() {\n" +
                "    let darkModeStyles = document.querySelector(\"#darktheme\");\n" +
                "    if (darkModeStyles == null)\n" +
                "        return;\n" +
                "    modeOption = localStorage.getItem(\"theme\");\n" +
                "    if (modeOption == 'light') {\n" +
                "        darkModeStyles.media = \"not all\";\n" +
                "    } else if (modeOption == 'dark') {\n" +
                "        darkModeStyles.media = \"all\";\n" +
                "    } else {\n" +
                "        darkModeStyles.media = \"all and (prefers-color-scheme: dark)\";\n" +
                "    }\n" +
                "}\n")
        +"updateTheme();\n"
    }
}