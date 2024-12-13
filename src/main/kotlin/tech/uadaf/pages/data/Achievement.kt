package tech.uadaf.pages.data

import dawnbreaker.data.raw.primary.Achievement
import kotlinx.html.DIV
import tech.uadaf.pages.bool
import tech.uadaf.pages.field
import tech.uadaf.pages.str

fun DIV.achievement(x: Achievement) = dataPage(x) {
    if(x.isCategory) {
        field("Achievement category: ") { str(x.label) }
    } else {
        field("Label: ") { str(x.label) }
        field("Description: ") { str(x.descriptionUnlocked) }
        field("Category: ") { str(x.category) }
        field("Single description? ") { bool(x.singleDescription) }
        field("Validate on storefront? ") { bool(x.validateOnStorefront) }
    }
}