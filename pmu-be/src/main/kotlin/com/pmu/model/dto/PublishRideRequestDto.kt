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
)
data class SourceDetail(
    val name:String,
    val address:String,
    val location:Location
)
data class Location(
    val lat:Double,
    val lng:Double
)


data class SearchRideRequest(
    val source: Location,
    val destination:Location,
    val date:String,
)