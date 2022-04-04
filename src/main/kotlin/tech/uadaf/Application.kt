package tech.uadaf

import Config
import dawnbreaker.data.raw.Mod
import dawnbreaker.data.raw.Recipe
import dawnbreaker.data.raw.Verb
import dawnbreaker.dsl.SourceBuilder
import dawnbreaker.loadVanilla
import dawnbreaker.locale.Locale
import dawnbreaker.vanilla
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
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
val config = Json.decodeFromStream<Config>(Files.newInputStream(Path.of("config.json")))
val baseUrl
    get() = config.baseUrl
lateinit var content: Mod
val locales: MutableList<Locale> = mutableListOf()

private fun loadLocale(name: String) {
    locales.add(Locale.load(name, vanilla, Paths.get(config.contentDir).resolve("loc_$name")))
}

private fun prepareContent() {
    loadVanilla(Path.of(config.contentDir))
    content = vanilla
    content.sources["temporary_verbs"] = SourceBuilder().apply {
        verbs {
            content.recipes.asSequence()
                .map { it.actionid }
                .filter { it.isNotBlank() }
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
                it.alt.asSequence().plus(it.linked.asSequence())
            }.filter { content.lookup<Recipe>(it.id) == null }.forEach {
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
    loadLocale("en")
    loadLocale("ru")
    loadLocale("zh-hans")
    loadLocale("de")
    loadLocale("jp")
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
