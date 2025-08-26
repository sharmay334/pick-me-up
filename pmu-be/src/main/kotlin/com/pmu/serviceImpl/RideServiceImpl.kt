package com.pmu.serviceImpl

import com.pmu.client.MapHttpClient
import com.pmu.client.MongoClient
import com.pmu.model.dto.PublishRideRequestDto
import com.pmu.model.dto.SearchRideRequest
import com.pmu.model.dto.SourceDetail
import com.pmu.model.dto.Location
import com.pmu.service.RideService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import io.ktor.http.parameters
import org.bson.Document
import org.bson.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent

class RideServiceImpl : RideService, KoinComponent {
    private val httpClient by KoinJavaComponent.inject<MapHttpClient>(MapHttpClient::class.java)

    private val collectionName = "rides"
    private val dbClient by inject<MongoClient>()

    override suspend fun createRideRequest(rideRequestDto: PublishRideRequestDto): String {
        val collection = dbClient.getCollection(collectionName)

        // Check if ride already exists with the same rideId
        val existingRide = collection.find(Document("rideId", rideRequestDto.rideId)).first()
        if (existingRide != null) {
            throw IllegalStateException("Ride with ID ${rideRequestDto.rideId} already exists")
        }

        // Convert DTO to Document
        val doc = Document()
            .append("_id", rideRequestDto._id)
            .append("userId", rideRequestDto.userId)
            .append("rideId", rideRequestDto.rideId)
            .append("source", Document()
                .append("name", rideRequestDto.source.name)
                .append("address", rideRequestDto.source.address))
            .append("destination", Document()
                .append("name", rideRequestDto.destination.name)
                .append("address", rideRequestDto.destination.address))
            .append("publishDate", rideRequestDto.publishDate)
            .append("publishTime", rideRequestDto.publishTime)
            .append("noOfPassengers", rideRequestDto.noOfPassengers)
            .append("createdAt", rideRequestDto.createdAt)
            .append("modifierAt", rideRequestDto.modifierAt)
            .append("isRideRequestActive", rideRequestDto.isRideRequestActive)
            .append("isRideCompleted", rideRequestDto.isRideCompleted)

        // Insert the document
        collection.insertOne(doc)
        return rideRequestDto._id
    }
    suspend fun getAllRideRequests(rideRequest: SearchRideRequest): List<PublishRideRequestDto> {
        val collection = dbClient.getCollection(collectionName)
        val query = Document("publishDate", rideRequest.date)

        return collection.find(query).map { doc ->
            PublishRideRequestDto(
                _id = doc.getString("_id"),
                userId = doc.getString("userId"),
                rideId = doc.getLong("rideId"),
                source = SourceDetail(
                    name = doc.get("source", Document::class.java).getString("name"),
                    address = doc.get("source", Document::class.java).getString("address"),
                    location = Location(
                        lat = doc.get("source", Document::class.java).getDouble("lat"),
                        lng = doc.get("source", Document::class.java).getDouble("lng")
                    )
                ),
                destination = SourceDetail(
                    name = doc.get("destination", Document::class.java).getString("name"),
                    address = doc.get("destination", Document::class.java).getString("address"),
                    location = Location(
                        lat = doc.get("destination", Document::class.java).getDouble("lat"),
                        lng = doc.get("destination", Document::class.java).getDouble("lng")
                    )
                ),
                publishDate = doc.getString("publishDate"),
                publishTime = doc.getString("publishTime"),
                noOfPassengers = doc.getInteger("noOfPassengers"),
                createdAt = doc.getString("createdAt"),
                modifierAt = doc.getString("modifierAt"),
                isRideRequestActive = doc.getBoolean("isRideRequestActive", true),
                isRideCompleted = doc.getBoolean("isRideCompleted", false)
            )
        }.toList()
    }
    override suspend fun findRides(rideRequest: SearchRideRequest): List<PublishRideRequestDto> {
        val rides = getAllRideRequests(rideRequest)
        val records =  rides.filter { it.isRideRequestActive }
        findNearbyRiders(Pair(rideRequest.source.lat,rideRequest.source.lng), Pair(rideRequest.destination.lat,rideRequest.destination.lng),riders = records,apiKey = "AIzaSyC6e4LPya_FZ0297z4H0ZBgnmUMzVd51eA")

    }
    fun findNearbyRiders(requestedSource: Pair<Double, Double>,
                         requestedTarget: Pair<Double, Double>,
                         riders: List<PublishRideRequestDto>, apiKey: String): List<PublishRideRequestDto> {
        val nearbyRiders = mutableListOf<PublishRideRequestDto>()

        for (rider in riders) {
            val sourceDist = getDistanceInMeters(
                "${requestedSource.first},${requestedSource.second}",
                "${rider.source.location.lat},${rider.source.location.lng}",
                apiKey
            )

            val targetDist = getDistanceInMeters(
                "${requestedTarget.first},${requestedTarget.second}",
                "${rider.destination.location.lat},${rider.destination.location.lng}",
                apiKey
            )

            if (sourceDist in 0..100 && targetDist in 0..100) {
                nearbyRiders.add(rider)
            }
        }

        return nearbyRiders
    }
    suspend fun getDistanceInMeters(origin: String, destination: String, apiKey: String): Int {
        val response =
            httpClient.getClient().get("https://maps.googleapis.com/maps/api/distancematrix/json") {
                parameters {
                    parameter("origins",origin)
                    parameter("destination",destination)
                    parameter("apiKey",apiKey)
                }
            }.body<JsonObject>()
        val distance = response["rows"]
            .asJsonArray[0]
            .asJsonObject["elements"]
            .asJsonArray[0]
            .asJsonObject["distance"]
            .asJsonObject["value"]
            .asInt
        print("data is ${response}")
        return 1
    }
}