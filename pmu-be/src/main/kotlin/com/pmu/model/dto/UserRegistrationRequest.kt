package com.pmu.model.dto

data class UserRegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

data class UserPhoneVerificationRequest(
    val phoneNumber: String,
    val otpCode: String?=""
)
