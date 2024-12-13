package tech.uadaf.pages.indices

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.Element
import dawnbreaker.locale.data.ElementLocale
import kotlinx.css.div
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.aspect
import tech.uadaf.csdata.elementPage
import tech.uadaf.csdata.missingFor
import tech.uadaf.csdata.verb
import tech.uadaf.pages.*

fun HEAD.workstationHead() {
    meta("twitter:card", "summary")
    "Book of Hours: Workstations".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "Workstations index".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (verb("_x")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}


//val specialQualities = listOf("persistent", "numen", "weather")
fun DIV.workstationPage() = div {
    id = "workstation-index"
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Workstations"
        }
    }
    table {
        id = "index-list"
        thead {
            tr {
                td { +"Workstation" }
                td { +"Accepts powers" }
                td { +"Accepts extra" }
                td { +"Special" }
            }
        }
        tbody {
            content.verbs.filter {
                it.slots.size == 5
            }.forEach { ws ->
                assert(ws.slots[0].essential.size == 1 && ws.slots[0].essential["ability"] == 1) { "Workstation heuristic bork" }
                assert(ws.slots[1].essential.size == 1 && ws.slots[1].essential["skill"] == 1) { "Workstation heuristic bork" }
                assert(ws.slots[2].essential.size == 1 && ws.slots[2].essential["memory"] == 1) { "Workstation heuristic bork" }
                tr("index-workstation") {
                    td("index-ref") { verbRef(ws.id) }
                    td("index-powers") {
                        ws.hints.forEach { k ->
//                            powerRef(k, v.toString())
                            img(k, aspect(k), classes = "index-icon") { }
//                            br {}
                        }
                    }
                    td("index-special") {
                        sequenceOf(
                            ws.slots[3].required.keys,
                            ws.slots[3].essential.keys,
                            ws.slots[4].required.keys,
                            ws.slots[4].essential.keys
                        )
                            .flatten()
                            .sorted()
                            .forEach { k ->
                                if (!content.lookup<Element>(k)!!.isHidden) {
                                    powerRef(k, 1)
                                }
                            }
                    }
                    td("index-special") {
                        ws.aspects.filterKeys { "difficulty" !in it }.forEach { (k, v) -> elementRef(k, v.toString()) }
                    }
                }
            }
        }
    }
}