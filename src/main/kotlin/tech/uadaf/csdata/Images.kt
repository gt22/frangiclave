package tech.uadaf.csdata

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.*
import tech.uadaf.baseUrl
import tech.uadaf.config
import tech.uadaf.content
import tech.uadaf.pages.period
import tech.uadaf.theme

fun image(path: String, space: String = config.imageDir) = "$baseUrl/static/$space/${path.removePrefix("/").removeSuffix(".png")}.png"

fun element(icon: String) = image("elements/$icon")

fun book(icon: String) = image("books/$icon")

fun bookspine(icon: String) = image("books/${icon}_")

fun thing(icon: String) = image("things/$icon")

fun comfort(icon: String) = image("comforts/$icon")

fun wallart(icon: String) = image("wallarts/$icon")

fun candle(icon: String) = image("candles/$icon")

fun aspect(icon: String) = image("aspects/$icon")

fun burnimage(icon: String) = image("burnimages/$icon")

fun ending(icon: String) = image("endings/$icon")

fun legacy(icon: String) = image("legacies/$icon")

fun verb(icon: String) = image("verbs/$icon")

fun frangiclave(icon: String) = image("frangiclave/$icon", "images")

private fun tryIcon(icon: String, id: String) = icon.ifBlank { id }

fun missingFor(x: Data) = when(x) {
        is Element -> if(x.isAspect) aspect("_x") else element("_x")
        is Verb -> verb("_x")
        is Achievement -> element("_x")
        else -> null
}

fun forData(x: Data) : String? = when(x) {
        is Element -> if (x.isAspect) aspect(tryIcon(x.icon, x.id)) else element(tryIcon(x.icon, x.id))
        is Ending -> ending(x.image)
        is Legacy -> legacy(x.image)
        is Verb -> verb(tryIcon(x.icon, x.id))
        is Portal -> verb(tryIcon(x.icon, x.id))
        is Recipe -> if(x.burnimage.isNotBlank()) burnimage(x.burnimage) else if(x.icon.isNotBlank()) verb(x.icon) else null
        is Achievement -> {
            val icon = x.iconUnlocked.ifBlank { content.lookup<Achievement>(x.category)?.iconUnlocked ?: "" }
            content.lookup<Element>(icon)?.let { forData(it) } ?: element(icon)
        }
        is Room -> aspect(x.period)
        else -> null
    }