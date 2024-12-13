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
import tech.uadaf.pages.*

fun HEAD.skillsHead() {
    meta("twitter:card", "summary")
    "Book of Hours: Skills".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "Skill index".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (aspect("skill")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}

//fun FlowContent.powerRef(id: String, amount: String = "1") = a(elementPage(id), classes = "power-ref ref") {
////    span("power-ref ref-text ref-amount") { +amount }
////    img(id, aspect(id), classes = "power-ref ref-icon")
//}
val skillQualities = listOf(
    "skill.language",
    "skill.chandlery",
    "effective.contamination.corruption",
    "effective.contamination.curse",
    "effective.contamination.infestation",
    "effective.contamination.theoplasma"
)

fun DIV.skillsPage() = div {
    id = "skills-index"
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Skills"
        }
    }
    table {
        id = "index-list"
        thead {
            tr {
                td { elementRef("skill") }
                td { +"Powers" }
                td { +"Special" }
                td { +"Wisdom" }
                td { +"Soul part" }
                td { +"Upgrading station" }
            }
        }
        tbody {
            classes = setOf("skills")
            content.elements.filter {
                "skill" in it.aspects
            }.forEach { sk ->
                val wisdoms = sk.aspects.keys.filter { it.startsWith("w.") }.map { it.removePrefix("w.") }
                tr("index-skill") {
                    td("index-ref") {
                        rowSpan = "2"
                        elementRef(sk.id)
                    }
                    td("index-powers") {
                        rowSpan = "2"
                        sk.aspects.filterKeys { it in powers }.forEach { (k, v) -> powerRef(k, v) }
                    }
                    td("index-special") {
                        rowSpan = "2"
                        // very long ids, so reference as powers (janky)
                        sk.aspects.filterKeys { it in skillQualities }.forEach { (k, v) -> powerRef(k, v) }
                    }
                    wisdomRow(sk, wisdoms[0])
                }
                tr("index-skill") {
                    wisdomRow(sk, wisdoms[1])
                }
            }
        }
    }
}

fun TR.wisdomRow(sk: Element, wisdom: String) {
    td("index-wisdom") {
        elementRef("w.$wisdom", "1")
    }
    val soul = content.lookup<Recipe>("commit.${wisdom.substring(0 until 3)}.${sk.id}")!!.effects.keys.first()
    td("index-soulpart") {
        elementRef(soul, "1")
    }
    td("index-workstations") {
        content.verbs
            .filter { w -> "e.$wisdom" in w.aspects }
            .filter { w -> sk.aspects.keys.filter { it in powers }.any { it in w.hints } }
            .filter { w -> content.lookup<Element>(soul)!!.aspects.keys.filter { it in powers }.any { it in w.hints } }
            .forEach { w ->
                verbRef(w.id, "1")
                br { }
            }
    }
}