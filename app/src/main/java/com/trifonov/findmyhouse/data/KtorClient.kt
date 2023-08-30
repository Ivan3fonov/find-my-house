package com.trifonov.findmyhouse.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class KtorClient {

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                explicitNulls = false
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    suspend fun getHouseAdresess(q: String): FeatureCollection {
        return client.get("https://api-adresse.data.gouv.fr/search/?type=housenumber")
        {
            parameter("q", q)
        }.body()
    }
}