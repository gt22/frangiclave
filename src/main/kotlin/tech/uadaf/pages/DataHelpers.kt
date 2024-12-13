package tech.uadaf.pages

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.*
import dawnbreaker.data.raw.secondary.*
import dawnbreaker.locale.LocaleData
import dawnbreaker.locale.data.ElementLocale
import dawnbreaker.locale.data.RecipeLocale
import dawnbreaker.locale.data.SlotLocale
import dawnbreaker.locale.data.VerbLocale
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
    println(base)
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

@Suppress("UNCHECKED_CAST")
fun <T : Data, L : LocaleData<T>> localizeInline(x: T, get: (L) -> String) =
    locales.joinToString("$$") { runCatching { get(it[x] as L) }.getOrElse { "" } }

fun FlowContent.elementRef(id: String, amount: String = "1") = a(elementPage(id), classes = "element-ref ref") {
    val element = content.lookup<Element>(id)
    if (element != null) {
        title = localizeInline(element) { a: ElementLocale -> a.label }
    }
    if (amount != "1") {
        span("element-ref ref-text ref-amount") { +amount }
    }
    img("", if (element != null) elementRefIcon(element) else aspect("_x"), classes = "element-ref ref-icon") {
        onError = "this.src='${if (element != null) missingFor(element) else aspect("_x")}'"
    }
    span("element-ref ref-text ref-id") { +id }
}

fun elementRefIcon(element: Element): String? {
    return when (element.manifestationtype.lowercase()) {
        "book" -> aspect("readable")
        "thing" -> aspect("thing")
        "comfort" -> aspect("comfort")
        "wallart" -> aspect("wallart")
        "candle" -> aspect("candle")
        else -> forData(element)
    }
}

fun FlowContent.verbRef(id: String, amount: String = "1") = a(verbPage(id), classes = "verb-ref ref") {
    val verb = content.lookup<Verb>(id)
    if (verb != null) {
        title = localizeInline(verb) { a: VerbLocale -> a.label }
    }
    if (amount != "1") {
        span("verb-ref ref-text ref-amount") { +amount }
    }
    img("", if (verb != null) forData(verb) else verb(id), classes = "verb-ref ref-icon") {
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

fun FlowContent.recipeRef(id: String, amount: String = "", challenges: List<String> = emptyList(), text: String = id) =
    a(recipePage(id), classes = "recipe-ref ref") {
        val recipe = content.lookup<Recipe>(id)
        if (recipe != null) {
            title = localizeInline(recipe) { a: RecipeLocale -> a.label }
        }
        if (amount.isNotBlank()) {
            span("recipe-ref ref-text ref-amount") { +amount }
        }
        img("", frangiclave("ritual"), classes = "recipe-ref ref-icon") {}
        span("recipe-ref ref-text ref-id") { +text }
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
    img("", frangiclave("library"), classes = "deck-ref ref-icon") {}
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
        "mutate" -> elementRef(x.id, "Mutate ${mutationLevel(x)}")
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

fun FlowContent.imm(req: Pair<String, String>, effect: Pair<String, String>) {
    elementRef(req.first, req.second)
    + " -> "
    elementRef(effect.first, effect.second)
}

val Imm.req : Pair<String, String>
    get() {
        check(reqs.size == 1)
        return reqs.entries.first().let { (k, v) -> k to v }
    }

fun FlowContent.imms(x: List<Imm>) {
    if (x.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            x.asSequence().sortedBy { it.req.first }.forEach { imm ->
                imm.effects.asSequence().sortedBy { (k, _) -> k }.forEach { (k, v) ->
                    li { imm(imm.req, k to v) }
                }
            }
        }
    }
}

fun FlowContent.immsOn(x: List<Pair<Pair<String, String>, Pair<String, String>>>) {
    if (x.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            x.asSequence().sortedBy { it.first.first }.forEach { (r, e) ->
                li { imm(r, e) }
            }
        }
    }
}



fun mutationLevel(level: String, additive: Boolean): String = if (additive && (level.isNotBlank() && level != "0" && level[0] != '-')) "+${level}" else level

fun mutationLevel(x: Mutation): String = mutationLevel(x.level, x.additive)

fun mutationLevel(x: XTrigger): String = mutationLevel(x.level.toString(), x.additive)
fun FlowContent.mutation(x: Mutation) {
    elementRef(x.filter)
    +" -> "
    elementRef(x.mutate, mutationLevel(x))
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

fun FlowContent.simpleRef(id: String, type: String, page: (String) -> String, icon: (String) -> String) = a(page(id), classes = "$type-ref ref") {
    img("", icon(id), classes = "$type-ref ref-icon") {}
    span("$type-ref ref-text ref-id") { +id }
}

fun FlowContent.portalRef(id: String) = a(portalPage(id), classes = "portal-ref ref") {
    img("", verb(id), classes = "portal-ref ref-icon") {}
    span("portal-ref ref-text ref-id") { +id }
}

fun FlowContent.endingRef(id: String) = a(endingPage(id), classes = "ending-ref ref") {
    img("", frangiclave("winter"), classes = "ending-ref ref-icon") {}
    span("ending-ref ref-text ref-id") { +id }
}

fun FlowContent.legacyRef(id: String) = a(legacyPage(id), classes = "legacy-ref ref") {
    img("", frangiclave("grail"), classes = "legacy-ref ref-icon") {}
    span("legacy-ref ref-text ref-id") { +id }
}

fun FlowContent.cultureRef(id: String) = a(culturePage(id), classes = "culture-ref ref") {
    img("", element("scholarvak"), classes = "culture-ref ref-icon") {}
    span("culture-ref ref-text ref-id") { +id }
}

fun FlowContent.dictaRef(id: String) = a(dictaPage(id), classes = "dicta-ref ref") {
    img("", element("secrethistories"), classes = "dicta-ref ref-icon") {}
    span("dicta-ref ref-text ref-id") { +id }
}

fun FlowContent.achievementRef(id: String) = simpleRef(id, "achievement", ::achievementPage) { element("trophy") }
fun FlowContent.roomRef(id: String) = simpleRef(id, "room", ::roomPage) { aspect(content.lookup<Room>(id)?.period ?: "period.curia") }

val Room.period: String
    get() = aspects.keys.first()

fun FlowContent.dataRef(x: Data) = when (x) {
    is Achievement -> achievementRef(x.id)
    is Deck -> deckRef(x.id)
    is Element -> elementRef(x.id)
    is Ending -> endingRef(x.id)
    is Legacy -> legacyRef(x.id)
    is Recipe -> recipeRef(x.id)
    is Verb -> verbRef(x.id)
    is Culture -> cultureRef(x.id)
    is Dicta -> dictaRef(x.id)
    is Portal -> portalRef(x.id)
    is Room -> roomRef(x.id)
    else -> throw IllegalArgumentException("Unknown data type: ${x::class.simpleName}")
}

fun UL.slot(x: Slot, short: Boolean = false) = li {
    subfield("Label: ") { localizations(x) { s: SlotLocale -> s.label } }
    subfield("Description: ") { localizations(x) { s: SlotLocale -> s.description } }
    subfield("Essential: ") { elementList(x.essential) }
    subfield("Required: ") { elementList(x.required) }
    subfield("Forbidden: ") { elementList(x.forbidden) }
    if(!short) {
        subfield("Greedy? ") { bool(x.greedy) }
        subfield("Consumes? ") { bool(x.consumes) }
        subfield("If aspects present: ") { elementList(x.ifaspectspresent) }
    }
}

fun FlowContent.slots(vararg slots: Slot, short: Boolean = false) {
    if (slots.isEmpty()) {
        em { +"None" }
    } else {
        ul {
            slots.forEach { slot(it, short) }
        }
    }
}

fun FlowContent.bool(x: Boolean) = span("bool-field") {
    if (x) +"Yes" else +"No"
}