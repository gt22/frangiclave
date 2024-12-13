package tech.uadaf.pages.indices

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.Element
import dawnbreaker.data.raw.primary.Recipe
import dawnbreaker.locale.data.ElementLocale
import kotlinx.css.div
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.aspect
import tech.uadaf.csdata.elementPage
import tech.uadaf.csdata.missingFor
import tech.uadaf.csdata.recipePage
import tech.uadaf.pages.*
import tech.uadaf.pages.data.manifest

fun HEAD.craftingHead() {
    meta("twitter:card", "summary")
    "Book of Hours: Crafting".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "Crafting index".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (aspect("forge")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}

//val skillQualities = listOf(
//    "skill.language",
//    "skill.chandlery",
//    "effective.contamination.corruption",
//    "effective.contamination.curse",
//    "effective.contamination.infestation",
//    "effective.contamination.theoplasma"
//)

data class Craft(
    val recipe: Recipe,
    val result: Element,
    val power: Pair<Element, Int>,
    val skill: Element,
    val special: Element?
)

val crafts: Map<Element, Map<Element?, Map<Pair<Element, Int>, List<Craft>>>> by lazy {
    val allcrafts = content.recipes.filter {
        it.id.startsWith("craft.")
    }.filter { it.craftable }
        .map { recipe ->
            val result = recipe.effects.filter { (_, v) -> v.toInt() > 0 }.keys.first()
            val skill =
                recipe.requirements.filter { (k, _) -> "skill" in content.lookup<Element>(k)!!.aspects }.keys.first()
            val power = recipe.requirements.filter { (k, _) -> k in powers }
                .map { (k, v) -> content.lookup<Element>(k)!! to v.toInt() }.first()
            val special = recipe.requirements.filter { (k, _) ->
                k != "ability" && k !in powers && "skill" !in content.lookup<Element>(k)!!.aspects
            }.keys.firstOrNull()
            Craft(
                recipe,
                content.lookup<Element>(result)!!,
                power,
                content.lookup<Element>(skill)!!,
                special?.let { content.lookup<Element>(it) }
            )
        }.groupBy { it.result }
        .mapValues { (_, v) -> v.groupBy { it.special }.mapValues { (_, v) -> v.groupBy { it.power } } }
    allcrafts.mapValues { (result, byResult) ->
        byResult.mapValues { (special, bySpecial) ->
            bySpecial.mapValues { (power, byPower) ->
                val (aspect, level) = power
                byPower.filter { craft ->
                    allcrafts[result]!![special]!![aspect to (level - 5)]?.any { it.skill == craft.skill } != true
                }
            }.filter { (_, v) -> v.isNotEmpty() }
        }.filter { (_, v) -> v.isNotEmpty() }
    }.filter { (_, v) -> v.isNotEmpty() }
}

fun DIV.craftingPage() = div {
    id = "crafting-index"
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Crafting"
        }
    }
    table {
        id = "index-list"
        thead {
            tr {
                td { +"Image" }
                td { +"Special" }
                td { +"Power" }
                td { +"Skill" }
                td { +"Recipe" }
            }
        }
        tbody {
            crafts.forEach { (result, byResult) ->
                val n = byResult.map { (_, v) -> v.map { (_, vv) -> vv.size }.sum() }.sum()
                val m = byResult.map { (_, v) -> v.size }.sum()
                val k = byResult.size
                tr("index-craft") {
                    td("index-image") {
                        rowSpan = (n + m + k + 1).toString()
                        a(href = elementPage(result.id)) { manifest(result) }
                        br {}
                        elementRef(result.id)
                    }
                }
                byResult.forEach { (special, bySpecial) ->
                    tr {
                        td("index-special") {
                            rowSpan = (bySpecial.size + bySpecial.map { (_, v) -> v.size }.sum() + 1).toString()
                            if (special != null) {
                                elementRef(special.id, "1")
                            }
                        }
                    }
                    bySpecial.forEach { (power, byPower) ->
                        tr {
                            td("index-power") {
                                rowSpan = (byPower.size + 1).toString()
                                elementRef(power.first.id, power.second.toString())
                            }
                        }
                        byPower.forEach { cr ->
                            tr {
                                td("index-craft-skill") {
                                    elementRef(cr.skill.id, "1")
                                }
                                td("index-recipe") {
                                    recipeRef(cr.recipe.id, text = "Recipe")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
