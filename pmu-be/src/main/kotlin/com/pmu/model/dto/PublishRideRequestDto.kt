package com.pmu.model.dto

import java.util.UUID

data class PublishRideRequestDto(
    val _id:String= UUID.randomUUID().toString(),
    val userId: String,
    val rideId:Long,
    val source:SourceDetail,
    val destination :SourceDetail,
    val publishDate:String,
    val publishTime:String,
    val noOfPassengers:Int,
    val createdAt:String,
    val modifierAt:String,
    val isRideRequestActive:Boolean = true,
    val isRideCompleted:Boolean = false,
    val pathPolyline:String? = null
)
data class SourceDetail(
    val name:String,
    val address:String,
    val location:Location
)
data class Location(
    val lat: Number,
    val lng: Number
)


data class SearchRideRequest(
    val source: Location,
    val destination:Location,
    val date:String,
    val polyline:String
)