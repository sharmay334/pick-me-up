package com.pmu.util

object Utility {
    fun generateRandomOTP(): String {
        val otp = (100000..999999).random()
        return otp.toString()
    }
}