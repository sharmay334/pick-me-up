package com.pmu.config

import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val mongoModule = module {
    single<CoroutineDatabase> {
        MongoConfig.createDatabase()
    }
}
