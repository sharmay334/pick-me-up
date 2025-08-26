package com.pmu.service

import com.pmu.client.MapHttpClient
import com.pmu.serviceImpl.AuthServiceImpl
import com.pmu.serviceImpl.RideServiceImpl
import com.pmu.serviceImpl.SearchServiceImpl
import org.koin.dsl.module

// Dependency Injection com.pmu.module for services
val serviceDIModule = module {
    single { MapHttpClient() } // Provide MapHttpClient as a singleton
    single<SearchService> { SearchServiceImpl() } // Provide SearchServiceImpl as SearchService
    single<AuthService> { AuthServiceImpl() }
    single<RideService> { RideServiceImpl() }
}