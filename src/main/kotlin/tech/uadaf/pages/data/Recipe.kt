package tech.uadaf.pages.data

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.Element
import dawnbreaker.data.raw.Recipe
import dawnbreaker.data.raw.Verb
import dawnbreaker.locale.data.RecipeLocale
import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.em
import tech.uadaf.content
import tech.uadaf.csdata.endingPage
import tech.uadaf.pages.*

fun Recipe.toLink(): Pair<String, Pair<String, List<String>>> {
    val chance = if(chance != 0 && chance != 100) "$chance%" else ""
    val challenges = challenges.keys.toList()
    return id to (chance to challenges)
}

fun DIV.recipe(x: Recipe) = dataPage(x) {
    field("Label: ") { localizations(x) { r: RecipeLocale -> r.label } }
    field("Start Description: ") { localizations(x) { r: RecipeLocale -> r.startdescription } }
    field("Inherits: ") { if(x.inherits.isNotBlank()) { recipeRef(x.inherits) } else { +"None" } }
    field("Inherited by: ") { recipeList(content.recipes.filter { it.inherits == x.id }.associate { it.id to "" }) }
    field("Description: ") { localizations(x) { r: RecipeLocale -> r.description } }
    field("Verb: ") { verbList(unfoldWildcard<Verb>(mapOf(x.actionid to 1))) }
    field("Requirements: ") { elementListS(x.requirements) }
    field("Table Requirements: ") { elementList(x.tablereqs) }
    field("Extant Requirements: ") { elementList(x.extantreqs) }
    field("Effects: ") { elementListS(x.effects) }
    field("Aspects: ") { elementList(x.aspects) }
    field("Global triggers: ") { elementList(x.xpans) }
    field("Mutation Effects: ") {
        mutations(x.mutations)
    }
    field("Purge: ") {
        elementList(unfoldWildcard<Element>(x.purge))
    }
    field("Halt Verbs: ") {
        verbList(unfoldWildcard<Verb>(x.haltverb))
    }
    field("Delete Verbs: ") {
        verbList(unfoldWildcard<Verb>(x.deleteverb))
    }
    field("Alternate Recipes: ") {
        val recipes = x.alt.asSequence()
            .unfoldWildcard()
            .filterNot { it.additional }
            .associate { it.toLink() }
        recipeListC(recipes)
    }
    field("Additional Recipes: ") {
        val recipes = x.alt.asSequence()
            .unfoldWildcard()
            .filter { it.additional }
            .associate { it.toLink() }
        recipeListC(recipes)
    }
    field("Linked Recipes: ") {
        val recipes = x.linked.asSequence()
            .unfoldWildcard()
            .associate { it.toLink() }
        recipeListC(recipes)
    }
    field("From Recipes: ") {
        val recipes = content.recipes.asSequence()
            .filter { it.linked.any { r -> wildcardMatch(r.id, x.id) } }
            .plus(content.recipes.asSequence().filter { it.alt.any { r -> wildcardMatch(r.id, x.id) } })
            .sortedBy { it.id }
            .associate { it.id to "" }
        recipeList(recipes)
    }
    field("Slots: ") { slots(*x.slots.toTypedArray()) }
    field("Warmup: ") { +x.warmup.toString() }
    field("Maximum executions: ") { +x.maxexecutions.toString() }
    field("Deck effects") {
        deckList(x.deckeffects.mapValues { it.value.toString() })
    }
    field("Internal Deck: ") {
        if(x.internaldeck == null) em { +"None" }
        else deckRef("internal:${x.id}")
    }
    field("Ending Flag: ") { if(x.ending.isNotBlank()) a(endingPage(x.ending)) { +x.ending } else em { +"None" } }
    field("Signal Ending Flavor: ") { str(x.signalEndingFlavour) }
    field("Portal: ") { if(x.portaleffect.isBlank()) em { +"None" } else portalRef(x.portaleffect) }
    field("Craftable? ") { bool(x.craftable) }
    field("Hint Only? ") { bool(x.hintonly) }
    field("Signal Important Loop? ") { bool(x.signalimportantloop) }
    field("Comments: ") { str(x.comments)}
}

inline fun <reified T : Data> unfoldWildcard(m: Map<String, Int>): Map<String, Int> = m.flatMap { (id, n) ->
    content.lookupWildcard<T>(id).map { it.id to n }
}.toMap()

inline fun <reified T : Data> Sequence<T>.unfoldWildcard(): Sequence<T> = flatMap {
    if(it.id.endsWith('*')) {
        content.lookupWildcard<T>(it.id)
    } else listOf(it)
}

fun wildcardMatch(a: String, b: String) = if(a == b) {
    true
} else if (a.endsWith('*')) {
    b.startsWith(a.substring(0, a.length - 1))
} else {
    false
}