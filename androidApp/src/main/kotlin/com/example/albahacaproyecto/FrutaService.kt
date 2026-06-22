package com.example.albahacaproyecto

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FrutaApiClient {
    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    suspend fun enviarFruta(fruta: Fruta): String {
        return try {
            val response = client.post("$BASE_URL/api/frutas") {
                contentType(ContentType.Application.Json)
                setBody(fruta)
            }
            response.bodyAsText()
        } catch (e: Exception) {
            "Error de conexión: No se pudo conectar con el backend."
        }
    }

    suspend fun obtenerFrutas(): String {
        return try {
            val response = client.get("$BASE_URL/api/frutas")
            response.bodyAsText()
        } catch (e: Exception) {
            "No hay registros en el servidor."
        }
    }
}

object LocalStorage {
    var ultimaFruta: Fruta? = null
}
