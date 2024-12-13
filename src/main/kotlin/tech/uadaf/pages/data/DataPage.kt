package tech.uadaf.pages.data

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.*
import kotlinx.html.*
import tech.uadaf.csdata.*

private fun description(data: Data) = when(data) {
    is Element -> data.description
    is Recipe -> data.startdescription
    is Deck -> data.description
    is Legacy -> data.startdescription
    is Ending -> data.description
    is Portal -> data.description
    is Verb -> data.description
    else -> ""
}

fun HEAD.dataHead(data: Data) {
    val type = data.javaClass.simpleName
    meta("twitter:card", "summary")
    "$type: ${data.id}".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    description(data).let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    (forData(data) ?: element("toolknockf")).let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}

fun DIV.dataPage(data: Data, copyText: String? = null, content: DIV.() -> Unit) = div {
    val type = data.javaClass.simpleName
    id = "data-page"
    h2 {
        id="content-title"
        span {
            id = "content-title-prefix"
            +type
            +": "
        }
        +data.id
        if(copyText != null) {
            div(classes = "copy-button data-copy ref") {
                attributes["data-clipboard-text"] = copyText
                img("", frangiclave("codex"), classes = "ref-icon") {}
                span("ref-text ref-id") { +"Copy" }
            }
        }
    }
    if(data is Element && data.manifestationtype != "") {
        manifest(data)
    } else {
        dataImage(data)
    }
    content()
}

fun FlowContent.dataImage(data: Data) {
    forData(data)?.let { image ->
        img("Icon", image, classes = "content-image image-${data.javaClass.simpleName.lowercase()} manifestation-empty") {
            missingFor(data)?.let { x ->
                onError = "this.src='$x'"
            }
        }
    }
}