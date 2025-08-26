package com.pmu.model.dto

import com.fasterxml.jackson.annotation.JsonAlias

data class UserAuthSessionResponseDto(
    @param:JsonAlias("name")
    val completeName: String,
    @param:JsonAlias("given_name")
    val firstName: String,
    @param:JsonAlias("family_name")
    val lastName:String,
    @param:JsonAlias("picture")
    val pictureUrl:String,
    val email:String,
    val phoneNumber:String? = null
)