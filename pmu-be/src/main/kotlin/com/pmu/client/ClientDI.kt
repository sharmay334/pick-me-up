package com.pmu.client


val clientDIModule = org.koin.dsl.module {
    single { MongoClient(connectionString = "mongodb://localhost:27017/") }
}