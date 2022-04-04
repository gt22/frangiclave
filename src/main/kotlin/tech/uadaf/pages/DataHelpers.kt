package tech.uadaf.pages

import dawnbreaker.data.raw.*
import dawnbreaker.locale.LocaleData
import dawnbreaker.locale.data.SlotLocale
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.*
import tech.uadaf.locales

fun FlowContent.field(name: String, content: P.() -> Unit) = p("content-field") {
    strong("field-title") {
        +name
    }
    content()
}

fun FlowContent.subfield(name: String, content: SPAN.() -> Unit) = span("content-subfield") {
    strong("subfield-title") {
        +name
    }
    content()
}

@Suppress("UNCHECKED_CAST")
fun <T : Data, L : LocaleData<T>> FlowContent.localizations(x: T, get: (L) -> String) {
    val base = runCatching { locales[0][x] as L }
    if (base.map(get).getOrElse { "" }.isNotBlank()) {
        ul("localizations") {
            locales.forEach {
                li("localizations-item") {
                    try {
                        +get(it[x] as L)
                    } catch (e: IllegalArgumentException) {
                        +"Unlocalized for ${it.name.uppercase()}"
                    }
                }
            }
        }
    } else {
        em { +"None" }
    }
}

fun FlowContent.str(x: String) {
    if (x.isNotBlank()) {
        +x
    } else {
        em { +"None" }
    }
}

fun FlowContent.elementRef(id: String, amount: String = "1") = a(aspectPage(id), classes = "element-ref ref") {
    val aspect = content.lookup<Element>(id)
    if (aspect != null) {
        title = aspect.label //TODO: Localization
    }
    if (amount != "1") {
        span("element-ref ref-text ref-amount") { +amount }
    }
    img(id, if (aspect != null) forData(aspect) else aspect("_x"), classes = "element-ref ref-icon") {
        onError = "this.src='${if (aspect != null) missingFor(aspect) else aspect("_x")}'"
    }
    span("element-ref ref-text ref-id") { +id }
}

fun FlowContent.verbRef(id: String, amount: String = "1") = a(verbPage(id), classes = "verb-ref ref") {
    val verb = content.lookup<Verb>(id)
    if (verb != null) {
        title = verb.label //TODO: Localization
    }
    if (amount != "1") {
        span("verb-ref ref-text ref-amount") { +amount }
    }
    img(id, if (verb != null) forData(verb) else verb(id), classes = "verb-ref ref-icon") {
        onError = "this.src='${if (verb != null) missingFor(verb) else verb("_x")}'"
    }
    span("verb-ref ref-text ref-id") { +id }
}

fun FlowContent.verbList(verbs: Map<String, Int>) = span("verb-ref ref-list") {
    if (verbs.isEmpty()) {
        em { +"None" }
    } else {
        verbs.forEach { verbRef(it.key, it.value.toString()) }
    }
}

fun FlowContent.elementListS(elements: Map<String, String>) = span("element-ref ref-list") {
    if (elements.isEmpty()) {
        em { +"None" }
    } else {
        elements.forEach { elementRef(it.key, it.value) }
    }
}

fun FlowContent.elementList(elements: Map<String, Int>) = elementListS(elements.mapValues { it.value.toString() })

fun FlowContent.recipeRef(id: String, amount: String = "", challenges: List<String> = emptyList()) =
    a(recipePage(id), classes = "recipe-ref ref") {
        val recipe = content.lookup<Recipe>(id)
        if (recipe != null) {
            title = recipe.label //TODO: Localization
        }
        if (amount.isNotBlank()) {
            span("recipe-ref ref-text ref-amount") { +amount }
        }
        img(id, aspect("ritual"), classes = "recipe-ref ref-icon") {}
        span("recipe-ref ref-text ref-id") { +id }
        challenges.forEach {
            img("Challenge: $it", aspect(it), classes = "recipe-ref ref-challenge") {
                onError = "this.src='${aspect("_x")}'"
            }
        }
    }

fun FlowContent.recipeListC(recipes: Map<String, Pair<String, List<String>>>) = span("recipe-ref ref-list") {
    if (recipes.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            recipes.forEach { (id, data) ->
                val (amount, challenges) = data
                li {
                    recipeRef(id, amount, challenges)
                }
            }
        }
    }
}

fun FlowContent.recipeList(recipes: Map<String, String>) = recipeListC(recipes.mapValues { it.value to emptyList() })

fun FlowContent.deckRef(id: String, amount: String = "1") = a(deckPage(id), classes = "deck-ref ref") {
    if (amount != "1") {
        span("deck-ref ref-text ref-amount") { +amount }
    }
    img(id, aspect("library"), classes = "deck-ref ref-icon") {}
    span("deck-ref ref-text ref-id") { +id }
}

fun FlowContent.deckList(decks: Map<String, String>) = span("deck-ref ref-list") {
    if (decks.isEmpty()) {
        em { +"None" }
    } else {
        decks.forEach { deckRef(it.key, it.value) }
    }
}

fun FlowContent.xtrigger(trigger: String, x: XTrigger) = span("xtrigger") {
    elementRef(trigger)
    +" -> "
    when (x.morpheffect.lowercase()) {
        "", "transform" -> elementRef(x.id)
        "spawn" -> elementRef(x.id, "Spawn ${x.level}")
        "mutate" -> elementRef(x.id, "Mutate ${if (x.level > 0) "+${x.level}" else x.level.toString()}")
    }
}

fun FlowContent.xtriggers(x: Map<String, List<XTrigger>>) {
    if (x.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            x.asSequence().sortedBy { it.key }.forEach { (trigger, effects) ->
                effects.asSequence().sortedBy { it.id }.forEach {
                    li { xtrigger(trigger, it) }
                }
            }
        }
    }
}

fun FlowContent.triggeredFrom(source: String, trigger: String) {
    elementRef(source)
    +" <- "
    elementRef(trigger)
}

fun FlowContent.trggeredFromList(triggers: List<Pair<String, String>>) {
    if (triggers.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            triggers.forEach {
                li { triggeredFrom(it.first, it.second) }
            }
        }
    }
}

fun FlowContent.mutation(x: Mutation) {
    elementRef(x.filter)
    +" -> "
    elementRef(x.mutate, if (x.additive && x.level > 0) "+${x.level}" else x.level.toString())
}

fun FlowContent.mutations(x: List<Mutation>) {
    if (x.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            x.forEach {
                li { mutation(it) }
            }
        }
    }
}

fun FlowContent.portalRef(id: String) = a(portalPage(id), classes = "portal-ref ref") {
    img(id, verb(id), classes = "portal-ref ref-icon") {}
    span("portal-ref ref-text ref-id") { +id }
}

fun FlowContent.endingRef(id: String) = a(endingPage(id), classes = "ending-ref ref") {
    img(id, aspect("winter"), classes = "ending-ref ref-icon") {}
    span("ending-ref ref-text ref-id") { +id }
}

fun FlowContent.legacyRef(id: String) = a(legacyPage(id), classes = "legacy-ref ref") {
    img(id, aspect("grail"), classes = "legacy-ref ref-icon") {}
    span("legacy-ref ref-text ref-id") { +id }
}

fun FlowContent.cultureRef(id: String) = a(culturePage(id), classes = "culture-ref ref") {
    img(id, element("scholarvak"), classes = "culture-ref ref-icon") {}
    span("culture-ref ref-text ref-id") { +id }
}

fun FlowContent.dictaRef(id: String) = a(dictaPage(id), classes = "dicta-ref ref") {
    img(id, element("secrethistories"), classes = "dicta-ref ref-icon") {}
    span("dicta-ref ref-text ref-id") { +id }
}

fun FlowContent.dataRef(x: Data) = when (x) {
    is Deck -> deckRef(x.id)
    is Element -> elementRef(x.id)
    is Ending -> endingRef(x.id)
    is Legacy -> legacyRef(x.id)
    is Recipe -> recipeRef(x.id)
    is Verb -> verbRef(x.id)
    is Culture -> cultureRef(x.id)
    is Dicta -> dictaRef(x.id)
    is Portal -> portalRef(x.id)
    else -> throw IllegalArgumentException("Unknown data type: ${x::class.simpleName}")
}

fun UL.slot(x: Slot) = li {
    subfield("Label: ") { localizations(x) { s: SlotLocale -> s.label } }
    subfield("Description: ") { localizations(x) { s: SlotLocale -> s.description } }
    subfield("Required: ") { elementList(x.required) }
    subfield("Forbidden: ") { elementList(x.forbidden) }
    subfield("Greedy? ") { bool(x.greedy) }
    subfield("Consumes? ") { bool(x.consumes) }
}

fun FlowContent.slots(vararg slots: Slot) {
    if (slots.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            slots.forEach { slot(it) }
        }
    }
}

fun FlowContent.bool(x: Boolean) = span("bool-field") {
    if (x) +"Yes" else +"No"
}