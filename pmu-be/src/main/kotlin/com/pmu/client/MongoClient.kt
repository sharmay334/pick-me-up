package com.pmu.client

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient as MongoDBClient
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.litote.kmongo.KMongo

class MongoClient(connectionString: String) {
    private val client: MongoDBClient = MongoClients.create(connectionString)
    private val database = client.getDatabase("pick-me-up")

    companion object {
        const val COLLECTION_NAME = "users"
    }

    fun getCollection(collectionName:String?): MongoCollection<Document> {
        return database.getCollection(collectionName?: COLLECTION_NAME)
    }

    fun <T> getCollection(collectionName: String, documentClass: Class<T>): MongoCollection<T> {
        return database.getCollection(collectionName, documentClass)
    }

    fun close() {
        client.close()
    }
}