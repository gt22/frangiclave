package tech.uadaf.pages.data

import dawnbreaker.data.raw.Recipe
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
    field("Label: ") { localizations(x.label) }
    field("Start Description: ") { localizations(x.startdescription) }
    field("Description: ") { localizations(x.description) }
    field("Verb: ") { if(x.actionid.isNotBlank()) verbRef(x.actionid) else em { +"None" } }
    field("Requirements: ") { elementListS(x.requirements) }
    field("Table Requirements: ") { elementList(x.tablereqs) }
    field("Extant Requirements: ") { elementList(x.extantreqs) }
    field("Effects: ") { elementListS(x.effects) }
    field("Aspects: ") { elementList(x.aspects) }
    field("Mutation Effects: ") {
        mutations(x.mutations)
    }
    field("Purge: ") {
        elementList(x.purge)
    }
    field("Halt Verbs: ") {
        verbList(x.haltverb)
    }
    field("Delete Verbs: ") {
        verbList(x.deleteverb)
    }
    field("Alternate Recipes: ") {
        val recipes = x.alt.asSequence()
            .filterNot { it.additional }
            .associate { it.toLink() }
        recipeListC(recipes)
    }
    field("Additional Recipes: ") {
        val recipes = x.alt.asSequence()
            .filter { it.additional }
            .associate { it.toLink() }
        recipeListC(recipes)
    }
    field("Linked Recipes: ") {
        val recipes = x.linked.associate { it.toLink() }
        recipeListC(recipes)
    }
    field("From Recipes: ") {
        val recipes = content.recipes.asSequence()
            .filter { it.linked.any { r -> r.id == x.id } }
            .plus(content.recipes.asSequence().filter { it.alt.any { r -> r.id == x.id } })
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