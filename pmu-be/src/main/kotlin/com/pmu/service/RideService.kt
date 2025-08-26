package com.pmu.service

import com.pmu.model.dto.PublishRideRequestDto
import com.pmu.model.dto.SearchRideRequest

interface RideService {
    suspend fun createRideRequest(rideRequestDto: PublishRideRequestDto): String
    suspend fun findRides(rideRequest: SearchRideRequest):List<Any>
}