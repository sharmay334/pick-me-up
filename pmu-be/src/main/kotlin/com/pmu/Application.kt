package com.pmu
import com.pmu.routes.configureSearchRouting
import com.pmu.serviceImpl.configureDapr
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDapr()
    configureFrameworks()
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureSearchRouting()

}
