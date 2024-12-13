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
import tech.uadaf.pages.elementRef
import tech.uadaf.pages.elementRefIcon
import tech.uadaf.pages.localizeInline
import tech.uadaf.pages.str

fun HEAD.memoryHead() {
    meta("twitter:card", "summary")
    "Book of Hours: Memories".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "Memory index".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (aspect("memory")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}

val powers = listOf("lantern", "forge", "edge", "winter", "heart", "grail", "moth", "knock", "rose", "sky", "moon", "scale", "nectar")

val specialQualities = listOf("persistent", "numen", "weather", "omen")
//fun FlowContent.powerRef(id: String, amount: String = "1") = a(elementPage(id), classes = "power-ref ref") {
////    span("power-ref ref-text ref-amount") { +amount }
////    img(id, aspect(id), classes = "power-ref ref-icon")
//}

fun FlowContent.powerRef(id: String, amount: Int = 1) = repeat(amount) {
    img(id, aspect(id), classes = "index-icon") {  }
}

fun DIV.memoryPage() = div {
    id = "memory-index"
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Memories"
        }
    }
    table {
        id = "index-list"
        thead {
            tr {
                td { elementRef("memory") }
                td { +"Powers" }
                td { +"Special" }
            }
        }
        tbody {
            content.elements.filter {
                "memory" in it.aspects && "lesson" !in it.aspects && !it.id.startsWith("_") && !it.id.startsWith(
                    "precursor"
                )
            }.forEach { mem ->
                tr("index-memory") {
                    td("index-ref") { elementRef(mem.id) }
                    td("index-powers") {
                        mem.aspects.filterKeys { it in powers }.forEach { (k, v) ->
//                            powerRef(k, v.toString())
                            powerRef(k, v)
//                            br {}
                        }
                    }
                    td("index-special") {
                        mem.aspects.filterKeys { it in specialQualities }.forEach { (k, v) ->
                            elementRef(k, v.toString())
                        }
                    }
                }
            }
        }
    }
}