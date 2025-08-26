package com.pmu

import com.pmu.client.clientDIModule
import com.pmu.config.mongoModule
import com.pmu.service.serviceDIModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(serviceDIModule, clientDIModule, mongoModule)
    }
}
