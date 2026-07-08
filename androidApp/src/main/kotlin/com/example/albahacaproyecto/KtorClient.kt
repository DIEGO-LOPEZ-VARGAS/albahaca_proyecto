package com.example.albahacaproyecto

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS COMPARTIDOS
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class LoginRequest(
    val usuario: String,
    val password: String
)

@Serializable
data class Fruta(
    val nombre: String,
    val cantidad: Int
)

@Serializable
data class Receta(
    val titulo: String,
    val ingredientes: String,
    val pasos: String
)

@Serializable
data class RouteInfo(
    val method: String,
    val path: String,
    val description: String
)

@Serializable
data class RailwayStatusResponse(
    val online: Boolean,
    val serverUrl: String,
    val latencyMs: Long,
    val routes: List<RouteInfo>
)

// ─────────────────────────────────────────────────────────────────────────────
// CLIENTE CENTRAL
// ─────────────────────────────────────────────────────────────────────────────

object KtorClient {
    const val BASE_URL = "https://backend-production-523ba.up.railway.app"
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
    suspend fun enviarLogin(usuario: String, contrasena: String): Int {
        return try {
            // TÉCNICA 6: Depuración de Peticiones Cliente-Servidor (Log de la URL)

            Log.d("DEPURACION_ALBAHACA", "Enviando POST a: $BASE_URL/login")
            val response = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(usuario, contrasena))
            }
            // TÉCNICA 3: Registro de Eventos (Logging para éxito)

            Log.d("DEPURACION_ALBAHACA", "RESPUESTA RECIBIDA: Status Code = ${response.status.value}")
            response.status.value
        } catch (e: Exception) {
            // TÉCNICA 8: Manejo de Excepciones (Exception Debugging)

            Log.e("DEPURACION_ALBAHACA", "--- FALLA DETECTADA ---")
            Log.e("DEPURACION_ALBAHACA", "Causa del error: ${e.message}")
            Log.e("DEPURACION_ALBAHACA", "Tipo de excepción: ${e.javaClass.simpleName}")
            0 // Retornamos 0 para manejar el error en la interfaz
        }
    }
}
