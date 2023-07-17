package tech.uadaf.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.CSSBuilder
import kotlinx.html.*
import tech.uadaf.pages.darktheme
import tech.uadaf.pages.lighttheme
import tech.uadaf.pages.stylesheet

fun Application.configureTemplating() {


    routing {
        get("/styles.css") {
            call.respondText(stylesheet, ContentType.Text.CSS)
        }

        get("/theme_light.css") {
            call.respondText(lighttheme, ContentType.Text.CSS)
        }

        get("/theme_dark.css") {
            call.respondText(darktheme, ContentType.Text.CSS)
        }
    }
}
