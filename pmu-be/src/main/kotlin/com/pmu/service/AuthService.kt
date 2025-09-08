package com.pmu.service

import com.pmu.model.dto.UserAuthSessionResponseDto
import com.pmu.model.dto.UserPhoneVerificationRequest
import com.pmu.model.dto.UserRegistrationRequest

interface AuthService{
    suspend fun performLogin(accessToken:String): UserAuthSessionResponseDto?
    suspend fun signUp(requestDto: UserRegistrationRequest): String?
    suspend fun generateOTP(phoneNumber: String)
    suspend fun validateOTP(request: UserPhoneVerificationRequest):String
}