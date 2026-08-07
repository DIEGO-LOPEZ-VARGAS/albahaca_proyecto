package com.example.albahacaproyecto

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    val id: Int = 0,
    @Transient val localId: Int = 0, // 🔥 Solo para control interno de Room
    val nombre: String,
    val cantidad: Int,
    @SerialName("fecha_caducidad") val fechaCaducidad: String = "",
    @SerialName("lugar_almacenamiento") val lugarAlmacenamiento: String = "Refri"
)

@Serializable
data class Receta(
    val id: Int = 0,
    @Transient val localId: Int = 0, // 🔥 Solo para control interno de Room
    val titulo: String,
    val ingredientes: String,
    val pasos: String
)

@Serializable
data class Producto(
    val id: Int,
    @SerialName("nombre_producto") val nombreProducto: String,
    val cantidad: Int,
    @SerialName("fecha_caducidad") val fechaCaducidad: String = "",
    @SerialName("tipo_almacenamiento") val tipoAlmacenamiento: String = "",
    val disponible: Boolean = true,
)

@Serializable
data class ProductosResponse(
    val rama: String = "",
    val total: Int = 0,
    val productos: List<Producto>,
)

@Serializable
data class RouteInfo(
    val method: String,
    val path: String,
    val description: String
)

@Serializable
data class Actividad(
    val id: Int,
    val accion: String,
    val detalle: String,
    val fecha: String
)

@Serializable
data class RailwayStatusResponse(
    val online: Boolean,
    val serverUrl: String,
    val latencyMs: Long,
    val routes: List<RouteInfo>
)

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

@Serializable
data class LoginResponse(
    val status: Int,
    val message: String,
    val token: String? = null,
    val role: String? = null,
    val nombre: String? = null
)

@Serializable
data class IngredientsRequest(
    val ingredientes: List<String>
)

@Serializable
data class NutricionResponse(
    val calorias: String = "",
    val proteinas: String = "",
    val grasas: String = "",
    val carbos: String = "",
    val consejo: String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// CLIENTE CENTRAL
// ─────────────────────────────────────────────────────────────────────────────

object KtorClient {
    const val BASE_URL = "https://backend-production-523ba.up.railway.app"
    
    // Variable para guardar el token de sesión
    var sessionToken: String? = null
        set(value) {
            field = value
            Log.d("DEPURACION_ALBAHACA", "sessionToken actualizado: ${if (value != null) "TOKEN_PRESENTE" else "NULL"}")
        }

    // Variable para guardar el nombre del usuario
    var userName: String? = null
        set(value) {
            field = value
            Log.d("DEPURACION_ALBAHACA", "userName actualizado: $value")
        }

    // Callback para manejar la expiración de sesión de forma global
    var onSessionExpired: (() -> Unit)? = null

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000 // Esperar hasta 60s por la IA
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }
    }
    
    suspend fun enviarLogin(usuario: String, contrasena: String): Int {
        return try {
            Log.d("DEPURACION_ALBAHACA", "Enviando POST a: $BASE_URL/login")
            val response = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(usuario, contrasena))
            }
            
            if (response.status.value == 200) {
                val loginData = response.body<LoginResponse>()
                sessionToken = loginData.token
                userName = loginData.nombre
                Log.d("DEPURACION_ALBAHACA", "TOKEN RECIBIDO: $sessionToken")
                Log.d("DEPURACION_ALBAHACA", "NOMBRE RECIBIDO: $userName")
            }
            
            Log.d("DEPURACION_ALBAHACA", "RESPUESTA RECIBIDA: Status Code = ${response.status.value}")
            response.status.value
        } catch (e: Exception) {
            Log.e("DEPURACION_ALBAHACA", "--- FALLA DETECTADA ---")
            Log.e("DEPURACION_ALBAHACA", "Causa del error: ${e.message}")
            0
        }
    }

    suspend fun enviarRegistro(nombre: String, email: String, contrasena: String): Int {
        return try {
            Log.d("DEPURACION_ALBAHACA", "Enviando POST a: $BASE_URL/register")
            val response = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(nombre, email, contrasena))
            }
            Log.d("DEPURACION_ALBAHACA", "RESPUESTA REGISTRO: ${response.status.value}")
            response.status.value
        } catch (e: Exception) {
            Log.e("DEPURACION_ALBAHACA", "Error en registro: ${e.message}")
            0
        }
    }

    suspend fun obtenerActividades(): List<Actividad> {
        return try {
            client.get("$BASE_URL/api/actividades") {
                sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
