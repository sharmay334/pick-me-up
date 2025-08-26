package com.pmu.model.dto

import com.fasterxml.jackson.annotation.JsonAlias


data class AutoCompleteResponseDto(
    @param:JsonAlias("predictions")
    val suggestions: List<Suggestions>
)

data class Suggestions(
    @param:JsonAlias("description")
    val address: String,
)