package tech.uadaf.csdata

import dawnbreaker.data.raw.*
import tech.uadaf.baseUrl

fun image(path: String) = "$baseUrl/static/images/${path.removePrefix("/").removeSuffix(".png")}.png"

fun element(icon: String) = image("elements/$icon")

fun aspect(icon: String) = image("aspects/$icon")

fun burnimage(icon: String) = image("burnimages/$icon")

fun ending(icon: String) = image("endings/$icon")

fun legacy(icon: String) = image("legacies/$icon")

fun verb(icon: String) = image("verbs/$icon")

private fun tryIcon(icon: String, id: String) = icon.ifBlank { id }

fun missingFor(x: Data) = when(x) {
        is Element -> if(x.isAspect) aspect("_x") else element("_x")
        is Verb -> verb("_x")
        else -> null
}

fun forData(x: Data) = when(x) {
        is Element -> if(x.isAspect) aspect(tryIcon(x.icon, x.id)) else element(tryIcon(x.icon, x.id))
        is Ending -> ending(x.image)
        is Legacy -> legacy(x.image)
        is Verb -> verb(tryIcon(x.icon, x.id))
        is Portal -> verb(tryIcon(x.icon, x.id))
        is Recipe -> if(x.burnimage.isNotBlank()) burnimage(x.burnimage) else null
        else -> null
    }