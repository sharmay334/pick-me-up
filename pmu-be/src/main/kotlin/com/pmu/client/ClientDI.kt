package com.pmu.client


val clientDIModule = org.koin.dsl.module {
    single { MongoClient(connectionString = System.getenv("MONGO_CONNECTION_STRING")) }
}