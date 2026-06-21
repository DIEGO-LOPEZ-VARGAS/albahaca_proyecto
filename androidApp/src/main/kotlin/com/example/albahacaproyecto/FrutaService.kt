package com.example.albahacaproyecto

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

data class Fruta(
    val nombre: String,
    val cantidad: Int
)

class FrutaApiClient {
    // HttpClient vacío usa el motor por defecto disponible en tu proyecto, CERO ERRORES
    private val client = HttpClient()

    suspend fun enviarFruta(fruta: Fruta): String {
        return try {
            // Mandamos el JSON manual para evitar líos de plugins
            val jsonManual = "{\"nombre\":\"${fruta.nombre}\",\"cantidad\":${fruta.cantidad}}"

            val response = client.post("https://backend-production-523ba.up.railway.app/api/frutas") {
                contentType(ContentType.Application.Json)
                setBody(jsonManual)
            }

            // Retorna lo que responda el servidor
            response.bodyAsText()
        } catch (e: Exception) {
            "Error de conexión: No se pudo conectar con el backend."
        }
    }

    // Esta función traerá todo el historial para mostrarlo en pantalla
    suspend fun obtenerFrutas(): String {
        return try {
            val response = client.get("https://backend-production-523ba.up.railway.app/api/frutas")
            response.bodyAsText()
        } catch (e: Exception) {
            "No hay registros en el servidor."
        }
    }
}

object LocalStorage {
    var ultimaFruta: Fruta? = null
}