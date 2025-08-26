package com.pmu.routes

import com.pmu.service.SearchService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get


fun Application.configureSearchRouting() {
    val searchService: SearchService = get()
    routing {
        route("/api/v1"){
            get("/autocomplete") {
                val searchText = call.request.queryParameters["keyword"]?:throw Exception("No keyword found")
                call.respond(searchService.autoComplete(searchText) as Any)
            }
        }
    }
}