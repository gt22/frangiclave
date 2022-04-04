package tech.uadaf.csdata

import dawnbreaker.data.raw.*
import tech.uadaf.baseUrl

fun page(type: String, id: String) = "$baseUrl/$type/$id"

fun deckPage(id: String) = page("deck", id)

fun aspectPage(id: String) = page("element", id)

fun elementPage(id: String) = page("element", id)

fun endingPage(id: String) = page("ending", id)

fun legacyPage(id: String) = page("legacy", id)

fun recipePage(id: String) = page("recipe", id)

fun verbPage(id: String) = if('*' in id) "$baseUrl/search?keywords=${id.substringBefore('*')}" else page("verb", id)

fun culturePage(id: String) = page("culture", id)

fun dictaPage(id: String) = page("dicta", id)

fun portalPage(id: String) = page("portal", id)

fun page(x: Data) = when(x) {
    is Deck -> deckPage(x.id)
    is Element -> elementPage(x.id)
    is Ending -> endingPage(x.id)
    is Legacy -> legacyPage(x.id)
    is Recipe -> recipePage(x.id)
    is Verb -> verbPage(x.id)
    is Culture -> culturePage(x.id)
    is Dicta -> dictaPage(x.id)
    is Portal -> portalPage(x.id)
    else -> throw IllegalArgumentException("Unknown data type: $x")
}