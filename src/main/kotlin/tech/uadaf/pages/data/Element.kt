package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Element
import dawnbreaker.locale.data.DeckLocale
import dawnbreaker.locale.data.ElementLocale
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.*
import tech.uadaf.pages.*

fun DIV.element(x: Element) = dataPage(x, x.textContent()) {
    field("Label: ") { localizations(x) { e: ElementLocale -> e.label } }
    field("Description: ") { localizations(x) { e: ElementLocale -> e.description } }
    field("Cross texts: ") {
        if (x.xexts.isEmpty()) {
            em { +"None" }
        } else {
            ul {
                x.xexts.forEach { (id, _) ->
                    li {
                        elementRef(id)
                        localizations(x) { s: ElementLocale -> s.xexts[id] ?: "None" }
                    }
                }
            }
        }
    }
    field("Inherits: ") {
        if (x.inherits.isNotBlank()) {
            elementRef(x.inherits)
        } else {
            +"None"
        }
    }
    field("Inherited by: ") { elementList(content.elements.filter { it.inherits == x.id }.associate { it.id to 1 }) }
    field("Aspects: ") { elementList(x.aspects) }
    field("Induces: ") {
        if (x.induces.isEmpty()) {
            em { +"None" }
        } else {
            val recipes = x.induces
                .map {
                    it.id to "${it.chance}%"
                }
                .sortedBy { it.first }
                .toMap()
            recipeList(recipes)
        }
    }
    field("Slots: ") { slots(*x.slots.toTypedArray()) }
    field("Commute: ") { elementList(x.commute.associateWith { 1 }) }
    field("Triggered by: ") { xtriggers(x.xtriggers) }
    field("Triggered from: ") {
        val triggers = content.elements.asSequence()
            .flatMap { source ->
                source.xtriggers.asSequence()
                    .flatMap { (trigger, effects) ->
                        effects.asSequence().filter { xt -> xt.id == x.id }
                            .map { source.id to trigger }
                    }
            }
            .sortedBy { it.first }
            .toList()
        trggeredFromList(triggers)
    }
    field("Triggers: ") {
        val triggers = content.elements.asSequence()
            .flatMap { source ->
                source.xtriggers.asSequence()
                    .filter { (trigger, _) -> trigger == x.id }
                    .map { (_, effect) -> source.id to effect }
            }.toMap()
        xtriggers(triggers)
    }
    field("Imms: ") { imms(x.imms) }
    field("Imms from: ") {
        val imms = content.elements.asSequence()
            .flatMap { source ->
                source.imms.asSequence()
                    .filter { x.id in it.effects }
                    .map { it.req to (source.id to it.effects[x.id]!!) }
            }.sortedBy { it.first.first }
            .toList()
        immsOn(imms)
    }
    field("Imms on: ") {
        val imms = content.elements.asSequence()
            .flatMap { source ->
                source.imms.asSequence()
                    .filter { x.id == it.req.first }
                    .flatMap { imm ->
                        imm.effects.asSequence().map { (k, v) -> (source.id to "1") to (k to v) }
                    }
            }
            .toList()
        immsOn(imms)
    }
    field("Requirements for Recipes: ") {
        val recipes = content.recipes.asSequence()
            .flatMap {
                sequence {
                    if (it.requirements.contains(x.id)) {
                        yield(it.id to it.requirements[x.id]!!)
                    }
                    if (it.tablereqs.contains(x.id)) {
                        yield(it.id to it.tablereqs[x.id]!!.toString() + " (table)")
                    }
                    if (it.extantreqs.contains(x.id)) {
                        yield(it.id to it.extantreqs[x.id]!!.toString() + " (anywhere)")
                    }
                }
            }
            .sortedBy { it.first }
            .toMap()
        recipeList(recipes)
    }
    field("Effect of Recipes: ") {
        val recipes = content.recipes.asSequence()
            .filter { it.effects.contains(x.id) }
            .map { it.id to it.effects[x.id]!! }
            .plus(content.recipes.asSequence().filter { it.aspects.contains(x.id) }
                .map { it.id to it.aspects[x.id].toString() })
            .plus(content.recipes.asSequence().filter { it.xpans.contains(x.id) }
                .map { it.id to it.xpans[x.id].toString() })
            .sortedBy { it.first }
            .toMap()
        recipeList(recipes)
    }
    field("Mutated by: ") {
        val mutations = content.recipes.flatMap { it.mutations.filter { m -> m.mutate == x.id }.map { m -> it to m } }
        if (mutations.isNotEmpty()) {
            mutations.forEach { (r, m) ->
                br { }
                elementRef(m.filter, "1")
                +" -> "
                recipeRef(r.id, mutationLevel(m))
            }
        } else {
            em { +"None" }
        }
    }
    field("Mutated in: ") {
        val mutations = content.recipes.flatMap {
            it.mutations.filter { m -> m.filter == x.id || x.aspects.keys.any { a -> m.filter == a } }
                .map { m -> it to m }
        }
        if (mutations.isNotEmpty()) {
            mutations.forEach { (r, m) ->
                br { }
                elementRef(m.mutate, mutationLevel(m))
                +" <- "
                recipeRef(r.id)
            }
        } else {
            em { +"None" }
        }
    }
    field("Referenced in Recipes: ") {
        val recipes = content.recipes.asSequence()
            .filter {
                sequenceOf(
                    it.mutations.asSequence().map { m -> m.level },
                    it.requirements.values.asSequence(),
                    it.extantreqs.values.asSequence(),
                    it.tablereqs.values.asSequence(),
                    it.deckeffects.values.asSequence(),
                    it.effects.values.asSequence()
                ).flatten().any { e -> e == x.id }
            }
            .map { it.id to "" }
            .sortedBy { it.first }
            .toMap()
        recipeList(recipes)
    }
    field("Lifetime: ") { if (x.lifetime > .0) +x.lifetime.toString() else em { +"None" } }
    field("Decay To: ") { if (x.decayTo.isNotBlank()) elementRef(x.decayTo, "1") else em { +"None" } }
    field("Burn To: ") { if (x.burnTo.isNotBlank()) elementRef(x.burnTo, "1") else em { +"None" } }
    field("Decay from: ") {
        val elements = content.elements.asSequence().filter { it.decayTo == x.id }.map { it.id to 1 }.toMap()
        elementList(elements)
    }
    field("Burn from: ") {
        val elements = content.elements.asSequence().filter { it.burnTo == x.id }.map { it.id to 1 }.toMap()
        elementList(elements)
    }
    field("Aspect? ") {
        bool(x.isAspect)
        val elements = content.elements.filter { it.aspects.contains(x.id) }.sortedBy { it.id }
            .associate { it.id to it.aspects[x.id]!! }
        val verbs = content.verbs.filter { it.aspects.contains(x.id) }.sortedBy { it.id }
            .associate { it.id to it.aspects[x.id]!! }
        if (elements.isNotEmpty()) {
            br { }
            elementList(elements)
        }
        if (verbs.isNotEmpty()) {
            br { }
            verbList(verbs)
        }
    }
    field("Unique? ") { bool(x.unique) }
    field("Uniqueness Group: ") { str(x.uniquenessgroup) }
    field("Hidden? ") { bool(x.isHidden) }
    field("No Art Needed? ") { bool(x.noartneeded) }
    field("Manifestation type: ") { str(x.manifestationtype) }
    field("Resaturate? ") { bool(x.resaturate) }
    field("Override Verb Icon: ") { str(x.verbicon) }
    field("In Decks: ") {
        val decks = content.decks.asSequence()
            .filter { it.spec.contains(x.id) }
            .map { it.id to it.spec.count { e -> e == x.id }.toString() }
            .toMap()
        deckList(decks)
    }
    field("Comments: ") { str(x.comments) }
}

fun Element.textContent() = """
    |$label
    |$description
    |
    |${xexts.values.joinToString("\n\n")}
""".trimMargin("|")

fun FlowContent.manifest(x: Element) {
    when (x.manifestationtype.lowercase()) {
        "book" -> manifestImage(x, ::book)
        "thing" -> manifestImage(x, ::thing)
        "comfort" -> manifestImage(x, ::comfort)
        "wallart" -> manifestImage(x, ::wallart)
        "candle" -> manifestImage(x, ::candle)
        else -> dataImage(x)
    }
}

fun FlowContent.manifestImage(x: Element, src: (String) -> String) {
    img(
        "Icon", src(x.id),
        classes = "content-image image-${x.javaClass.simpleName.lowercase()} manifestation-${x.manifestationtype}"
    ) {
        aspect("_x").let { x ->
            onError = "this.src='$x'"
        }
    }
}