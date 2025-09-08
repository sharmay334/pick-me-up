package com.pmu.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


data class DistanceMatrixComputeRouteDto(
    val origins:List<Origin>,
    val destinations:List<Origin>,
    val travelMode:String = "WALK"
)
data class Origin(
    val waypoint:WayPoint
)
data class WayPoint(
    val location:Loc
)
data class Loc(
    val latLng: LatLng
)
data class LatLng(
    val latitude: Number,
    val longitude: Number
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DistanceMatrixResponse(
    val originIndex:Int?=null,
    val destinationIndex:Int?=null,
    val status:Any?=null,
    val distanceMeters: Number?=0,
)
