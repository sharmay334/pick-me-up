package com.pmu.client

import com.pmu.serviceImpl.DaprProvider
import io.dapr.client.domain.StateOptions




object RedisClient {
    val client = DaprProvider.client
     fun save(key: String, value: String) {
         runCatching {
             client.saveState("statestore", key, value).block()
         }.onFailure {e->
             e.printStackTrace()
         }

    }
    fun get(key: String): String? {
        return client.getState("statestore", key, String::class.java).block()?.value
    }
    fun delete(key: String) {
        client.deleteState("statestore", key).block()
    }
}