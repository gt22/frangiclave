package tech.uadaf.pages.data

import dawnbreaker.data.raw.Element
import dawnbreaker.locale.data.ElementLocale
import kotlinx.html.DIV
import kotlinx.html.br
import kotlinx.html.em
import tech.uadaf.content
import tech.uadaf.pages.*

fun DIV.element(x: Element) = dataPage(x) {
    field("Label: ") { localizations(x) { e : ElementLocale -> e.label } }
    field("Description: ") { localizations(x) { e : ElementLocale -> e.description } }
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
    field("Cross Triggers: ") { xtriggers(x.xtriggers) }
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
    field("Triggered by: ") {
        val triggers = content.elements.asSequence()
            .flatMap { source ->
                source.xtriggers.asSequence()
                    .filter { (trigger, _) -> trigger == x.id }
                    .map { (_, effect) -> source.id to effect }
            }.toMap()
        xtriggers(triggers)
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
            .plus(content.recipes.asSequence().filter { it.aspects.contains(x.id) }.map { it.id to it.aspects[x.id].toString() })
            .sortedBy { it.first }
            .toMap()
        recipeList(recipes)
    }
    field("Lifetime: ") { if (x.lifetime != 0) +x.lifetime.toString() else em { +"None" } }
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
        val elements = content.elements.filter { it.aspects.contains(x.id) }.sortedBy { it.id }.associate { it.id to it.aspects[x.id]!! }
        if(elements.isNotEmpty()) {
            br { }
            elementList(elements)
        }
    }
    field("Unique? ") { bool(x.unique) }
    field("Uniqueness Group: ") { str(x.uniquenessgroup) }
    field("Hidden? ") { bool(x.isHidden) }
    field("No Art Needed? ") { bool(x.noartneeded) }
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