package com.example.albahacaproyecto

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(val usuario: String, val password: String)

object KtorClient {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun enviarLogin(usuario: String, contrasena: String): Int {
        return try {
            val response = client.post("http://10.0.2.2:8080/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(usuario, contrasena))
            }
            response.status.value
        } catch (e: Exception) {
            0 // 0 indica fallo de conexión
        }
    }
}