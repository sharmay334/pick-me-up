package com.pmu.serviceImpl

import com.pmu.client.MapHttpClient
import com.pmu.client.MongoClient
import com.pmu.client.RedisClient
import com.pmu.model.dto.OTPRequestDTO
import com.pmu.model.dto.UserAuthSessionResponseDto
import com.pmu.model.dto.UserPhoneVerificationRequest
import com.pmu.model.dto.UserRegistrationRequest
import com.pmu.service.AuthService
import com.pmu.util.Utility
import io.ktor.client.call.*
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import org.koin.java.KoinJavaComponent.inject

class AuthServiceImpl: AuthService {
    private val httpClient by inject<MapHttpClient>(MapHttpClient::class.java)
    private val dbClient by inject<MongoClient>(MongoClient::class.java)
    override suspend fun performLogin(accessToken: String): UserAuthSessionResponseDto? {
       return httpClient.getClient().get("https://openidconnect.googleapis.com/v1/userinfo"){
           headers{
               header("Authorization", "Bearer $accessToken")
           }
       }.also {
           pushUserRecord(it.body())
       }.body()
    }

    override suspend fun signUp(requestDto: UserRegistrationRequest): String? {
        val result = pushUserRecord(UserAuthSessionResponseDto(
            requestDto.firstName.plus(" ").plus(requestDto.lastName),
            requestDto.firstName,
            requestDto.lastName,
            "",
            requestDto.email,
            phoneNumber = requestDto.phoneNumber
        ))
        return if(result) "User registered successfully" else throw Exception("User already registered")
    }

    override suspend fun generateOTP(phoneNumber: String) {
        sendOTPMessage(phoneNumber)
    }

    override suspend fun validateOTP(request: UserPhoneVerificationRequest):String {
       if(request.otpCode != RedisClient.get("otp_${request.phoneNumber}")){
           throw Exception("Invalid OTP")
       }
        else
            RedisClient.delete("otp_${request.phoneNumber}")
        return "OK"
    }

    fun pushUserRecord(userAuth: UserAuthSessionResponseDto): Boolean{
        val collectionName = "users"  // Adding the missing collection name
        val collection = dbClient.getCollection(collectionName)
        val existingUser = collection.find(org.bson.Document("_id", "pickme_${userAuth.email.substringBeforeLast("@")}")).first()
        if (existingUser == null) {
            val doc = org.bson.Document()
                .append("_id", "pickme_${userAuth.email.substringBeforeLast("@")}")
                .append("completeName", userAuth.completeName)
                .append("firstName", userAuth.firstName)
                .append("lastName", userAuth.lastName)
                .append("pictureUrl", userAuth.pictureUrl)
                .append("email", userAuth.email)
                .append("phoneNumber",userAuth.phoneNumber.isNullOrBlank().not().let { userAuth.phoneNumber })
            collection.insertOne(doc)
            return true
        }
        return false
    }

    suspend fun sendOTPMessage(phoneNumber:String): Boolean{
        val otp = Utility.generateRandomOTP()
        httpClient.getClient().post("https://textlinksms.com/api/send-sms") {
            contentType(ContentType.Application.Json)
            headers{
                header("Authorization", "Bearer ndGc1olDL17VE4Tg7MmXqGTPtNgFIf6CiFii99SkEJklzXmvUVcKzKR9ryopRDaX")
            }
            setBody(OTPRequestDTO("+91${phoneNumber}", "Your OTP is ${otp}"))
        }
        RedisClient.save("otp_${phoneNumber}", otp)
        return true
    }

}