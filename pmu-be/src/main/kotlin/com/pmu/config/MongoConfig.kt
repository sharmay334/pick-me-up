package com.pmu.config

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoConfig {
    fun createDatabase(connectionString: String = "mongodb://localhost:27017/") =
        KMongo.createClient(connectionString)
            .coroutine
            .getDatabase("pick-me-up")
}
