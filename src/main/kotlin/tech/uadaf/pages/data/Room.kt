package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Room
import dawnbreaker.data.raw.secondary.Slot
import dawnbreaker.data.raw.primary.Verb
import dawnbreaker.dsl.SlotBuilder
import dawnbreaker.locale.data.RoomLocale
import dawnbreaker.locale.data.VerbLocale
import kotlinx.html.DIV
import tech.uadaf.pages.*

fun DIV.room(x: Room) = dataPage(x, x.textContent()) {
    field("Label: ") { localizations(x) { r: RoomLocale -> r.label } }
    field("Description: ") { localizations(x) { r: RoomLocale -> r.description } }
    field("Period: ") { elementRef(x.period) }
    field("Unlock: ") {
        slots(x.unlockSlot!!, short = true)
    }
}

fun Room.textContent() = "$label\n$description"