package com.pmu.service

import com.pmu.model.dto.AutoCompleteResponseDto

interface SearchService{
    suspend fun autoComplete(text: String): AutoCompleteResponseDto?
}