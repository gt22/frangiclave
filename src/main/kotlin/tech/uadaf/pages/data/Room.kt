package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Recipe
import dawnbreaker.data.raw.primary.Room
import dawnbreaker.data.raw.secondary.Slot
import dawnbreaker.data.raw.primary.Verb
import dawnbreaker.dsl.SlotBuilder
import dawnbreaker.locale.data.RecipeLocale
import dawnbreaker.locale.data.RoomLocale
import dawnbreaker.locale.data.VerbLocale
import dawnbreaker.vanilla
import kotlinx.html.DIV
import tech.uadaf.pages.*

fun DIV.room(x: Room) = dataPage(x, x.textContent()) {
    // Access source directly to bypass masking
    val baseRecipe = requireNotNull(vanilla.sources_["recipes/terrain.json"]!!.lookup<Recipe>("terrain.${x.id}")) {
        "Can't find unlock recipe for room ${x.id}"
    }
    field("Locked label: ") { localizations(baseRecipe) { r: RecipeLocale -> r.preface } }
    field("Locked description: ") { localizations(baseRecipe) { r: RecipeLocale -> r.startdescription } }
    field("Label: ") { localizations(baseRecipe) { r: RecipeLocale -> r.label } }
    field("Description: ") { localizations(baseRecipe) { r: RecipeLocale -> r.description } }
    field("Period: ") { elementRef(x.period) }
    field("Unlock: ") {
        slots(*baseRecipe.preslots.toTypedArray(), short = true)
    }
}

fun Room.textContent() = "$label\n$description"