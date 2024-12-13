package tech.uadaf.plugins

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import tech.uadaf.config

fun Application.configureMonitoring() {
    install(CallLogging) {
        logger = LoggerFactory.getLogger("Requests").also {
            if(it is Logger) {
                it.level = if(config.isDebug) ch.qos.logback.classic.Level.DEBUG else ch.qos.logback.classic.Level.INFO
            } else {
                it.error("Unable to set log level: Unexpected logger class")
            }
        }
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") && !call.request.path().startsWith("/static") }
    }

}
