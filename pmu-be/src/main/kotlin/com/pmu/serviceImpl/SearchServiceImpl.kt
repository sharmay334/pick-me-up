package com.pmu.serviceImpl

import com.pmu.client.MapHttpClient
import com.pmu.model.dto.AutoCompleteResponseDto
import com.pmu.service.SearchService
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.java.KoinJavaComponent.inject
import java.net.URLEncoder

class SearchServiceImpl : SearchService {
    companion object {
        private var GOOGLE_MAPS_API_KEY: String? = null
        init {
            GOOGLE_MAPS_API_KEY = System.getenv("GOOGLE_MAPS_API_KEY") ?: GOOGLE_MAPS_API_KEY
        }
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
        private const val COUNTRY = "in"
    }

     val httpClient by inject<MapHttpClient>(MapHttpClient::class.java)

      fun buildUrl(input: String): String {
        val encodedInput = URLEncoder.encode(input, Charsets.UTF_8.toString())
        return "$BASE_URL?key=$GOOGLE_MAPS_API_KEY&input=$encodedInput&components=country:$COUNTRY"
    }

    override suspend fun autoComplete(text: String): AutoCompleteResponseDto? {
        runCatching {
           return httpClient.getClient().get(buildUrl(text)).body()
        }.onFailure {
            throw Exception("No result found for $text")
        }
        return null
    }
}