package tech.uadaf.plugins

import dawnbreaker.data.raw.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.DIV
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.p
import tech.uadaf.content
import tech.uadaf.pages.basePage
import tech.uadaf.pages.data.*
import tech.uadaf.pages.index
import tech.uadaf.pages.search

inline fun <reified T : Data> Routing.dataPage(type: String, crossinline page : DIV.(T) -> Unit) = get("/$type/{id}") {
    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.NotFound)
    val data = content.lookup<T>(id) ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respondHtml { basePage("$type:$id", title = "${data.javaClass.simpleName}: ${data.id}", head = { dataHead(data) }) { page(data) } }
}

fun Application.configureRouting() {
    install(IgnoreTrailingSlash)
    routing {
        get("/") {
            call.respondHtml { index() }
        }

        get("/search") {
            call.respondHtml {
                search(call.parameters["keywords"] ?: "")
            }
        }

        dataPage<Verb>("verb") { verb(it) }
        dataPage<Deck>("deck") { deck(it) }
        dataPage<Ending>("ending") { ending(it) }
        dataPage<Legacy>("legacy") { legacy(it) }
        dataPage<Element>("element") { element(it) }
        dataPage<Recipe>("recipe") { recipe(it) }
        dataPage<Culture>("culture") { culture(it) }
        dataPage<Dicta>("dicta") { dicta(it) }
        dataPage<Portal>("portal") { portal(it) }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
        static("/static") {
            files("static")
        }
        // Backwards compatibility with old FC:
        static("/static/images/icons40/aspects") {
            files("static/images/aspects")
        }
        static("/static/images/burnImages") {
            files("static/images/burnimages")
        }
        static("/static/images/elementArt") {
            files("static/images/elements")
        }
        static("/static/images/endingArt") {
            files("static/images/endings")
        }
        static("/static/images/icons100/legacies") {
            files("static/images/legacies")
        }
        static("/static/images/icons100/verbs") {
            files("static/images/verbs")
        }
        install(StatusPages) {
            status(HttpStatusCode.NotFound) { call, _ ->
                call.respondHtml(HttpStatusCode.NotFound) {
                    basePage("") {
                        h2 {
                            id = "content-title"
                            +"Nothing?"
                        }
                        p {
                            +"Nothing"
                        }
                    }
                }
            }
        }
    }
}
