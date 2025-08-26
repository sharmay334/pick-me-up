package com.pmu

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureMonitoring() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
   /* val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-sample-server")

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)

        capturedRequestHeaders(HttpHeaders.UserAgent)

        spanKindExtractor {
            if (httpMethod == HttpMethod.Post) {
                SpanKind.PRODUCER
            } else {
                SpanKind.CLIENT
            }
        }

        attributesExtractor {
            onStart {
                attributes.put("start-time", System.currentTimeMillis())
            }
            onEnd {
                attributes.put("end-time", System.currentTimeMillis())
            }
        }
    }*/
    install(CallLogging) {
        callIdMdc("call-id")
    }
    routing {
        get("/hello") {
            call.respondText("Hello World!")
        }

        post("/post") {
            val postData = call.receiveText()
            call.respondText("Received: $postData")
        }
    }
}
