package com.pmu

import com.pmu.client.MapHttpClient
import com.pmu.model.dto.PublishRideRequestDto
import com.pmu.model.dto.SearchRideRequest
import com.pmu.model.dto.UserAuthSessionResponseDto
import com.pmu.model.dto.UserPhoneVerificationRequest
import com.pmu.model.dto.UserRegistrationRequest
import com.pmu.service.AuthService
import com.pmu.service.RideService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureSecurity() {
    val httpClient by inject<MapHttpClient>(MapHttpClient::class.java)
    val authService by inject<AuthService>(AuthService::class.java)
    val rideService by inject<RideService>(RideService::class.java)
    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "750050477438-m7cdvmc06omtcmseu6m1d7568j5oq6rc.apps.googleusercontent.com",
                    clientSecret = "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",
                    defaultScopes = listOf("openid", "email", "profile")
                )
            }
            client = httpClient.getClient()
        }
    }
   /* install(CSRF) {
        // tests Origin is an expected value
        allowOrigin("http://localhost:8080")

        // tests Origin matches Host header
        originMatchesHost()

        // custom header checks
        checkHeader("X-CSRF-Token")
    }*/
    routing {
        authenticate("auth-oauth-google") {
            get("login") {
                call.respondRedirect("/callback")
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                call.respond(
                    status = HttpStatusCode.OK,
                    message = authService.performLogin(principal?.accessToken.toString()) as UserAuthSessionResponseDto
                )
            }
        }
        route("api/v1") {
            post("/signup") {
                val userRequest = call.receive<UserRegistrationRequest>()
                call.respond(status = HttpStatusCode.Created, message = authService.signUp(userRequest) as String)
            }
            post("/verify/phone"){
                val request = call.receive<UserPhoneVerificationRequest>()
                call.respond(status = HttpStatusCode.OK, message = authService.generateOTP(request.phoneNumber))
            }
            post("/publish/phone/check"){}
        }
        post("/ride/search") {}
        post("/ride/create") {
            val rideRequest = call.receive<PublishRideRequestDto>()
            call.respond(status = HttpStatusCode.OK, message =rideService.createRideRequest(rideRequest))
        }
        post("/ride/find") {
            val rideRequest = call.receive<SearchRideRequest>()

        }
        post("/ride/update") {}
        post("/ride/delete") {}
        post("/user/me") {}
        post("/user/update") {}
        post("/user/delete") {}
        post("/user/history") {}
        post("/user/favourite") {}
        post("/user/invite") {}
        post("/user/invite/accept") {}
    }
}


