package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Dicta
import kotlinx.html.DIV
import tech.uadaf.pages.elementRef
import tech.uadaf.pages.field
import tech.uadaf.pages.str

fun DIV.dicta(x: Dicta) = dataPage(x) {
    field("World Sphere Type: ") { str(x.worldSphereType) }
    field("Default World Sphere Path: ") { str(x.defaultWorldSpherePath) }
    field("Master Scene: ") { str(x.masterScene) }
    field("Logo Scene: ") { str(x.logoScene) }
    field("Quote Scene: ") { str(x.quoteScene) }
    field("Menu Scene: ") { str(x.menuScene) }
    field("Playfield Scene: ") { str(x.playfieldScene) }
    field("Game Over Scene: ") { str(x.gameOverScene) }
    field("New Game Scene: ") { str(x.newGameScene) }
    field("Note Element: ") { elementRef(x.noteElementId) }
    field("Default Travel Duration: ") { str(x.defaultTravelDuration) }
    field("Default Quick Travel Duration: ") { str(x.defaultQuickTravelDuration) }
}