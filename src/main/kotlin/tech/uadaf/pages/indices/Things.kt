package tech.uadaf.pages.indices

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.Element
import dawnbreaker.locale.data.ElementLocale
import kotlinx.css.div
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.*
import tech.uadaf.pages.*
import tech.uadaf.pages.data.manifest

fun HEAD.thingsHead() {
    meta("twitter:card", "summary")
    "Book of Hours: Things".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "Things index".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (aspect("thing")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}


//val specialQualities = listOf("persistent", "numen", "weather")

val forms = listOf(
    "beast",
    "beverage",
    "blank",
    "cache",
    "light",
    "egg",
    "fabric",
    "flower",
    "fruit",
    "hanging",
    "key",
    "leaf",
    "liquid",
    "mark",
    "material",
    "remains",
    "root",
    "sustenance",
    "fuel",
    "metal",
    "wood",
    "stone",
    "cooperative",
    "gem",
    "glass",
    "tool",
    "ink",
    "device",
    "distributable",
    "encaustum",
    "brewable",
    "remains"
)

fun DIV.thingsPage() = div {
    id = "things-index"
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Things"
        }
    }
    table {
        id = "index-list"
        thead {
            tr {
                td { +"Image" }
                td { +"Thing" }
                td { +"Powers" }
                td { +"Forms" }
                td { elementRef("scrutiny") }
            }
        }
        tbody {
            content.elements.filter {
                "thing" in it.aspects && !it.id.startsWith("_") && "readable" !in it.aspects
            }.forEach { th ->
                tr("index-things") {
                    td("index-image") { manifest(th) }
                    td("index-ref") {
                        elementRef(th.id)
                    }
                    td("index-powers") {
                        th.aspects.filterKeys { it in powers }.forEach { (k, v) ->
//                            powerRef(k, v.toString())
                            powerRef(k, v)
//                            br {}
                        }
                    }
                    td("index-special") {
                        th.aspects.filterKeys { it in forms }.forEach { (k, v) ->
                            elementRef(k, v.toString())
                            br {  }
                        }
                    }
                    td("index-special") {
                        th.xtriggers["scrutiny"]?.filter { th.id != it.id }?.forEach {
                            elementRef(it.id, it.level.toString())
                        }
                    }
                }
            }
        }
    }
}