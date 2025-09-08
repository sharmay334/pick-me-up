package com.pmu.serviceImpl

import com.pmu.client.MongoClient
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping

object DaprProvider {
    val client: DaprClient by lazy {
        DaprClientBuilder()
            .build()
    }
}

fun Application.configureDapr():DaprClient {
    val daprClient = DaprProvider.client
    environment.monitor.subscribe(ApplicationStopping) {
        daprClient.close()
    }
    daprClient.waitForSidecar(5000).block()
    org.koin.dsl.module {
        single { daprClient }
    }
    return daprClient
}
