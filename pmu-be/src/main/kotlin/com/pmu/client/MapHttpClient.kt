package com.pmu.client

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.callid.CallId
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.utils.clientDispatcher
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.Dispatchers
@OptIn(InternalAPI::class)
class MapHttpClient{
    fun getClient(): HttpClient{
        return HttpClient(CIO){
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.ALL

            }
            install(HttpCache)
            install(ContentNegotiation){
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                }
            }
            install(CallId){
                addToHeader(HttpHeaders.XRequestId)

            }
            engine {
                dispatcher = Dispatchers.clientDispatcher(4)
                pipelining = true
            }
            install(HttpTimeout){
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 30000L
                socketTimeoutMillis = 30000L
            }
        }
    }
}