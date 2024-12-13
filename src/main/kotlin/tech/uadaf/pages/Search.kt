package tech.uadaf.pages

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.Deck
import dawnbreaker.data.raw.secondary.Slot
import dawnbreaker.locale.LocaleData
import dawnbreaker.locale.data.DeckLocale
import dawnbreaker.locale.data.SlotLocale
import kotlinx.css.label
import kotlinx.html.*
import tech.uadaf.content
import tech.uadaf.csdata.element
import tech.uadaf.locales
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

private fun find(kw: String): Sequence<Pair<Data, List<String>>> =
    findInternal(kw)
        .groupingBy { it.first }
        .fold(emptyList<String>()) { acc, (_, m) -> acc + m }
        .asSequence().map { (d, m) -> d to m }


private fun findInternal(kw: String): Sequence<Pair<Data, List<String>>> = sequence {
    if (kw.isBlank()) return@sequence
    yieldAll(find(kw, content.decks) {
        sequence {
            yield(id)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.elements) {
        sequence {
            yield(id)
            yield(uniquenessgroup)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.endings) {
        sequence {
            yield(id)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.legacies) {
        sequence {
            yield(id)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.recipes) {
        sequence {
            yield(id)
            yield(comments)
        }
    })
    yieldAll(find(kw, content.verbs) {
        sequence {
            yield(id)
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
    yieldAll(find(kw, content.achievements) {
        sequence {
            yield(id)
            yield(label)
            yield(descriptionUnlocked)
        }
    })
    yieldAll(find(kw, content.rooms) {
        sequence {
            yield(id)
            yield(label)
            yield(description)
            yield(unlockSlot!!.label)
            yield(unlockSlot!!.description)
        }
    })
    locales.forEach { l ->
        yieldAll(findl(kw, l.decks) {
            sequence {
                yield(label)
                yield(description)
                yieldAll(drawmessages.values)
            }
        })
        yieldAll(findl(kw, l.elements) {
            sequence {
                yield(label)
                yield(description)
                yieldAll(xexts.values)
                slots.forEach { yieldSlot(it) }
            }
        })
        yieldAll(findl(kw, l.endings) {
            sequence {
                yield(label)
                yield(description)
            }
        })
        yieldAll(findl(kw, l.legacies) {
            sequence {
                yield(label)
                yield(startdescription)
                yield(description)
            }
        })
        yieldAll(findl(kw, l.recipes) {
            sequence {
                yield(preface)
                yield(startlabel)
                yield(label)
                yield(startdescription)
                yield(description)
                slots.forEach { yieldSlot(it) }
            }
        })
        yieldAll(findl(kw, l.verbs) {
            sequence {
                yield(label)
                yield(description)
                slot?.let { yieldSlot(it) }
                slots.forEach { yieldSlot(it) }
            }
        })
    }
}

private suspend fun SequenceScope<String>.yieldSlot(s: SlotLocale) {
    yield(s.label)
    yield(s.description)
}

private fun <T : Data> find(kw: String, data: List<T>, f: T.() -> Sequence<String>) =
    find(kw, data.asSequence().filter { "%" !in it.id }.map { it to it.f() })

private inline fun <reified T : Data, L : LocaleData<T>> findl(
    kw: String,
    data: List<L>,
    crossinline f: L.() -> Sequence<String>
) =
    findl(kw, data.asSequence().filter { "%" !in it.id }.map { it to it.f() })

private fun find(kw: String, data: Sequence<Pair<Data, Sequence<String>>>): Sequence<Pair<Data, List<String>>> =
    sequence {
        data.sortedBy { (d, _) -> d.id }.forEach { (d, item) ->
            val matches = findMatches(kw, item)
            if (matches.isNotEmpty()) {
                yield(d to matches)
            }
        }
    }

private inline fun <reified T : Data> findl(
    kw: String,
    data: Sequence<Pair<LocaleData<T>, Sequence<String>>>
): Sequence<Pair<Data, List<String>>> =
    sequence {
        data.sortedBy { (d, _) -> d.id }.forEach { (d, item) ->
            val matches = findMatches(kw, item)
            if (matches.isNotEmpty()) {
                yield(content.lookup<T>(d.id)!! to matches)
            }
        }
    }

private fun findMatches(kw: String, item: Sequence<String>): List<String> = sequence {
    for (field in item) {
        val start = field.lowercase().indexOf(kw)
        if (start < 0) continue
        val end = start + kw.length
        val match = StringBuilder().apply {
            if (start > 30) {
                append("...")
            }
            append(field.substring(max(start - 30, 0), start).trimStart())
            append(field.substring(start, end))
            append(field.substring(end, min(end + 30, field.length)).trimEnd())
            if (end + 30 < field.length) {
                append("...")
            }
        }.toString()
        yield(match)
    }
}.toList()