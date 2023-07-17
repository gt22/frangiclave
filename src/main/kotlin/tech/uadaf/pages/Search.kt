package tech.uadaf.pages

import dawnbreaker.data.raw.Data
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.element
import kotlin.math.max
import kotlin.math.min

fun HTML.search(keywords: String) = basePage(title = "Search: $keywords", keywords = keywords, head = {
    "Search: $keywords".let { title ->
        meta("og:title", title)
        meta("twitter:title", title)
    }
    "".let { desc ->
        meta("description", desc)
        meta("og:description", desc)
        meta("twitter:description", desc)
    }
    element("toolknockf").let {
        meta("twitter:image", it)
        meta("og:image", it)
    }
}) {
    val res = find(keywords.lowercase().trim())
    h2 {
        id = "content-title"
        span {
            id = "content-title-prefix"
            +"Search: "
        }
        +keywords
    }
    res.forEach { (d, matches) ->
        div("search-result") {
            h3("search-result-title") {
                dataRef(d)
            }
            ul {
                matches.forEach { li { +it } }
            }
        }
    }
}

private fun find(kw: String): Sequence<Pair<Data, List<String>>> = sequence {
    if (kw.isBlank()) return@sequence
    yieldAll(find(kw, content.decks) {
        sequence {
            yield(id)
            yield(label)
            yield(description)
            yield(comments)
            yieldAll(drawmessages.values)
        }
    })
    yieldAll(find(kw, content.elements) {
        sequence {
            yield(id)
            yield(label)
            yield(description)
            yield(uniquenessgroup)
            yield(comments)
            yieldAll(xexts.values)
        }
    })
    yieldAll(find(kw, content.endings) {
        sequence {
            yield(id)
            yield(label)
            yield(description)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.legacies) {
        sequence {
            yield(id)
            yield(label)
            yield(startdescription)
            yield(description)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.recipes) {
        sequence {
            yield(id)
            yield(label)
            yield(startdescription)
            yield(description)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.verbs) {
        sequence {
            yield(id)
            yield(label)
            yield(description)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.portals) {
        sequence {
            yield(id)
            yield(label)
            yield(otherworldId)
            yield(egressId)
            yield(description)
            yieldAll(consequences.map { it.toPath })
        }
    })
}

private fun <T : Data> find(kw: String, data: List<T>, f: T.() -> Sequence<String>) =
    find(kw, data.asSequence().map { it to it.f() })

private fun find(kw: String, data: Sequence<Pair<Data, Sequence<String>>>): Sequence<Pair<Data, List<String>>> =
    sequence {
        data.sortedBy { (d, _) -> d.id }.forEach { (d, item) ->
            val matches = sequence {
                for (field in item) {
                    val start = field.lowercase().indexOf(kw)
                    if (start < 0) continue
                    val end = start + kw.length
                    val match = StringBuilder().apply {
                        if (start > 30) { append("...") }
                        append(field.substring(max(start - 30, 0), start).trimStart())
                        append(field.substring(start, end))
                        append(field.substring(end, min(end + 30, field.length)).trimEnd())
                        if (end + 30 < field.length) { append("...") }
                    }.toString()
                    yield(match)
                }
            }.toList()
            if (matches.isNotEmpty()) {
                yield(d to matches)
            }
        }
    }