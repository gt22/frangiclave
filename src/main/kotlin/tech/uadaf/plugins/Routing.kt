package tech.uadaf.plugins

import dawnbreaker.data.raw.Data
import dawnbreaker.data.raw.primary.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
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
import tech.uadaf.pages.indices.*
import tech.uadaf.pages.search
import java.io.File

inline fun <reified T : Data> Routing.dataPage(type: String, crossinline page : DIV.(T) -> Unit) = get("/$type/{id}") {
    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.NotFound)
    println("IDDD: " + id)
    val data = content.lookup<T>(id) ?: return@get call.respond(HttpStatusCode.NotFound)
    println(data)
    call.respondHtml { basePage("$type:$id", title = "${data.javaClass.simpleName}: ${data.id}", head = { dataHead(data) }) { page(data) } }
}

fun Routing.dataPages() {
    dataPage<Achievement>("achievement") { achievement(it) }
    dataPage<Verb>("verb") { verb(it) }
    dataPage<Deck>("deck") { deck(it) }
    dataPage<Ending>("ending") { ending(it) }
    dataPage<Legacy>("legacy") { legacy(it) }
    dataPage<Element>("element") { element(it) }
    dataPage<Recipe>("recipe") { recipe(it) }
    dataPage<Room>("room") { room(it) }
    dataPage<Culture>("culture") { culture(it) }
    dataPage<Dicta>("dicta") { dicta(it) }
    dataPage<Portal>("portal") { portal(it) }
}

fun Routing.indexPages() {
    get("/memories") { call.respondHtml { basePage(title = "Memory index", sidebar = false, head = { memoryHead() }) { memoryPage() }}}
    get("/workstations") { call.respondHtml { basePage(title = "Workstation index", sidebar = false, head = { workstationHead() }) { workstationPage() }}}
    get("/things") { call.respondHtml { basePage(title = "Things index", sidebar = false, head = { thingsHead() }) { thingsPage() }}}
    get("/skills") { call.respondHtml { basePage(title = "Skills index", sidebar = false, head = { skillsHead() }) { skillsPage() }}}
    get("/crafts") { call.respondHtml { basePage(title = "Crafting index", sidebar = false, head = { craftingHead() }) { craftingPage() }}}
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

        get("/robots.txt") {
            call.respondText { "User-agent: *\nDisallow: /" }
        }

        dataPages()
        indexPages()

        staticResources("/static", "static")
        staticFiles("/static", File("static"))
        // Backwards compatibility with old FC:
        staticFiles("/static/images/icons40/aspects", File("static/images/aspects"))
        staticFiles("/static/images/burnImages", File("static/images/burnimages"))
        staticFiles("/static/images/elementArt", File("static/images/elements"))
        staticFiles("/static/images/endingArt", File("static/images/endings"))
        staticFiles("/static/images/icons100/legacies", File("static/images/legacies"))
        staticFiles("/static/images/icons100/verbs", File("static/images/verbs"))
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
