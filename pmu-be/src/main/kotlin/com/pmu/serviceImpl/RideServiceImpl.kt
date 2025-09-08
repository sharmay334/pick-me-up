package com.pmu.serviceImpl

import com.pmu.client.MapHttpClient
import com.pmu.client.MongoClient
import com.pmu.model.dto.DistanceMatrixComputeRouteDto
import com.pmu.model.dto.DistanceMatrixResponse
import com.pmu.model.dto.LatLng
import com.pmu.model.dto.Loc
import com.pmu.model.dto.PublishRideRequestDto
import com.pmu.model.dto.SearchRideRequest
import com.pmu.model.dto.SourceDetail
import com.pmu.model.dto.Location
import com.pmu.model.dto.Origin
import com.pmu.model.dto.WayPoint
import com.pmu.service.RideService
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bson.Document
import org.bson.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.index

class RideServiceImpl : RideService, KoinComponent {
    private var GOOGLE_MAPS_API_KEY: String? = null

    init {
        GOOGLE_MAPS_API_KEY = System.getenv("GOOGLE_MAPS_API_KEY")
    }
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
                .append("address", rideRequestDto.source.address)
                .append("location", Document()
                .append("lat", rideRequestDto.source.location.lat)
                .append("lng", rideRequestDto.source.location.lng)))

            .append("destination", Document()
                .append("name", rideRequestDto.destination.name)
                .append("address", rideRequestDto.destination.address)
            .append("location", Document()
                .append("lat", rideRequestDto.destination.location.lat)
                .append("lng", rideRequestDto.destination.location.lng)))
            .append("destinationLatLng", Document())
            .append("publishDate", rideRequestDto.publishDate)
            .append("publishTime", rideRequestDto.publishTime)
            .append("noOfPassengers", rideRequestDto.noOfPassengers)
            .append("createdAt", rideRequestDto.createdAt)
            .append("modifierAt", rideRequestDto.modifierAt)
            .append("isRideRequestActive", rideRequestDto.isRideRequestActive)
            .append("isRideCompleted", rideRequestDto.isRideCompleted)
            .append("pathPolyline", rideRequestDto.pathPolyline)

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
                        lat = doc.get("source", Document::class.java).get("location", Document::class.java) .getDouble("lat"),
                        lng = doc.get("source", Document::class.java).get("location", Document::class.java).getDouble("lng")
                    )
                ),
                destination = SourceDetail(
                    name = doc.get("destination", Document::class.java).getString("name"),
                    address = doc.get("destination", Document::class.java).getString("address"),
                    location = Location(
                        lat = doc.get("destination", Document::class.java).get("location", Document::class.java).getDouble("lat"),
                        lng = doc.get("destination", Document::class.java).get("location", Document::class.java).getDouble("lng")
                    )
                ),
                publishDate = doc.getString("publishDate"),
                publishTime = doc.getString("publishTime"),
                noOfPassengers = doc.getInteger("noOfPassengers"),
                createdAt = doc.getString("createdAt"),
                modifierAt = doc.getString("modifierAt"),
                isRideRequestActive = doc.getBoolean("isRideRequestActive", true),
                isRideCompleted = doc.getBoolean("isRideCompleted", false),
                pathPolyline = doc.getString("pathPolyline")
            )
        }.toList()
    }
    override suspend fun findRides(rideRequest: SearchRideRequest): List<PublishRideRequestDto> {
        val rides = getAllRideRequests(rideRequest)
        val records =  rides.filter { it.isRideRequestActive }
        return findNearbyRiders(Pair(rideRequest.source.lat.toDouble(),rideRequest.source.lng.toDouble()), Pair(rideRequest.destination.lat.toDouble(),rideRequest.destination.lng.toDouble()),riders = records,apiKey = GOOGLE_MAPS_API_KEY?:"")
    }
    suspend fun findNearbyRiders(requestedSource: Pair<Double, Double>,
                         requestedTarget: Pair<Double, Double>,
                         riders: List<PublishRideRequestDto>, apiKey: String): List<PublishRideRequestDto> = coroutineScope{
        val requestDto1 = async { generateComputeRouteRequest(requestedSource,riders) }
        val requestDto2 = async { generateComputeRouteRequest(requestedTarget,riders,false) }
        val sourceNearbyData = async { getDistanceInMeters(requestDto1.await(),apiKey) }
        val targetNearbyData = async { getDistanceInMeters(requestDto2.await(),apiKey) }
        val result = mutableListOf<PublishRideRequestDto>()
        val recordMap = mutableMapOf<String,PublishRideRequestDto>()
        sourceNearbyData.await().forEachIndexed {index, it ->
            if((it.distanceMeters?.toLong() ?: 0) <= 500){
                recordMap[riders[index]._id] = riders[index]
            }
        }
        targetNearbyData.await().forEachIndexed {index, it ->
            if((it.distanceMeters?.toLong() ?: 0) <= 500){
                recordMap[riders[index]._id] = riders[index]
            }
        }
        recordMap.forEach { (_, ride) -> result.add(ride) }

        return@coroutineScope result
    }
    suspend fun getDistanceInMeters(requestDto:DistanceMatrixComputeRouteDto, apiKey: String): List<DistanceMatrixResponse> {
        val response =
            httpClient.getClient().post("https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix") {
                contentType(io.ktor.http.ContentType.Application.Json)
                headers{
                    header("X-Goog-Api-Key",apiKey)
                    header("X-Goog-FieldMask","originIndex,destinationIndex,distanceMeters,status")
                }
                setBody(requestDto)
            }.body<List<DistanceMatrixResponse>>()
        return response
    }
     fun generateComputeRouteRequest(requestedSource: Pair<Double, Double>,
                                            riders: List<PublishRideRequestDto>,isSource: Boolean?=true): DistanceMatrixComputeRouteDto {
        val destinations = mutableListOf<Origin>()
        riders.forEach {
           if(isSource == true){
               destinations.add(Origin(WayPoint(Loc(LatLng(it.source.location.lat,it.source.location.lng)))))
           }
            else
               destinations.add(Origin(WayPoint(Loc(LatLng(it.destination.location.lat,it.destination.location.lng)))))
        }
        return DistanceMatrixComputeRouteDto(
            origins = listOf(Origin(WayPoint(Loc(LatLng(requestedSource.first,requestedSource.second))))),
            destinations = destinations
        )
    }
}