package tech.uadaf

import Config
import dawnbreaker.data.raw.Mod
import dawnbreaker.data.raw.primary.Element
import dawnbreaker.data.raw.primary.Recipe
import dawnbreaker.data.raw.primary.Verb
import dawnbreaker.dsl.SourceBuilder
import dawnbreaker.loadVanilla
import dawnbreaker.locale.Locale
import dawnbreaker.vanilla
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import tech.uadaf.plugins.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.collections.MutableList
import kotlin.collections.asSequence
import kotlin.collections.mutableListOf
import kotlin.collections.set


@OptIn(ExperimentalSerializationApi::class)
val config = Json.decodeFromStream<Config>(Files.newInputStream(Paths.get("config.json")))

@Serializable
data class Theme(
    val id: String,
    val title: String,
    val description: String,
    val logo: String,
    val icon: String
)

@OptIn(ExperimentalSerializationApi::class)
val theme = Json.decodeFromStream<Theme>(::Theme::class.java.getResourceAsStream("/themes/${config.theme}.json")!!)
val baseUrl
    get() = config.baseUrl
lateinit var content: Mod
val locales: MutableList<Locale> = mutableListOf()

private fun loadLocales() {
    Files.list(Paths.get(config.contentDir))
        .filter { it.fileName.toString().startsWith("loc_") }
        .forEach {
            locales.add(Locale.load(it.fileName.toString().removePrefix("loc_"), vanilla, it))
        }
}

private fun prepareContent() {
    loadVanilla(Paths.get(config.contentDir))
    content = vanilla
    content.elements.forEach {
        check(it.imms.all { it.reqs.size == 1 }) { "Multi-req imm in ${it.id}" }
    }
    content.sources.remove("rooms.json")
    content.applyInherits()
    content.sources["temporary_verbs"] = SourceBuilder().apply {
        verbs {
            content.recipes.asSequence()
                .map { it.actionid }
                .filter { it.isNotBlank() }
                .filterNot { '*' in it }
                .filter { content.lookup<Verb>(it) == null }
                .distinct()
                .forEach {
                    +Verb(it)
                }
        }
    }.t
    content.sources["internal_recipes"] = SourceBuilder().apply {
        recipes {
            content.recipes.asSequence().flatMap {
                it.alt.asSequence() + it.linked.asSequence() + it.lalt.asSequence()
            }.filterNot { it.id.endsWith('*') }
                .filter { content.lookup<Recipe>(it.id) == null }.forEach {
                    +it
                }
        }
    }.t
    content.sources["internal_decks"] = SourceBuilder().apply {
        decks {
            content.recipes.asSequence().filter { it.internaldeck != null }.forEach {
                it.internaldeck!!.id = "internal:${it.id}"
                +it.internaldeck!!
            }
        }
    }.t
    content.sources["salon_markers"] = SourceBuilder().apply {
        aspects {
            content.recipes.asSequence()
                .flatMap { it.mutations.map { m -> m.mutate } }
                .filter { it.startsWith("%") }
                .toSet()
                .forEach { sln ->
                    aspect(sln) {
                        isHidden = true
                    }
                }
        }
    }.t
    loadLocales()
}

fun main() {
    prepareContent()
    embeddedServer(Netty, port = config.port, host = config.host) {
        configureRouting()
        configureHTTP()
        configureMonitoring()
        configureTemplating()
        configureSerialization()
    }.start(wait = true)
}
