package com.example.albahacaproyecto

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class FrutaApiClient {
    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    suspend fun enviarFruta(fruta: Fruta): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/frutas") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(fruta)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerFrutas(): List<Fruta> {
        return try {
            client.get("$BASE_URL/api/frutas") {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

object LocalStorage {
    var ultimaFruta: Fruta? = null
}
