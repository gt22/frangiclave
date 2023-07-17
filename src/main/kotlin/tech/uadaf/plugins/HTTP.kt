package tech.uadaf.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import tech.uadaf.config

fun Application.configureHTTP() {
    install(CachingHeaders) {
        options { _, outgoingContent ->
            if(config.isDebug) return@options null
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.Html, ContentType.Image.PNG, ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10 * 24 * 60 * 60))
                else -> null
            }
        }
    }
    install(Compression) {
        gzip {}
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

}
