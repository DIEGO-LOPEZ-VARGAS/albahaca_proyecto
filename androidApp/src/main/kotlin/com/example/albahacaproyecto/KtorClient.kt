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
import kotlinx.serialization.json.Json

object KtorClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun enviarLogin(
        usuario: String,
        contrasena: String
    ): Int {

        return try {

            val jsonBody = """
                {
                    "usuario":"$usuario",
                    "password":"$contrasena"
                }
            """.trimIndent()

            val response = client.post("https://backend-production-523ba.up.railway.app/login") {

                contentType(ContentType.Application.Json)

                setBody(jsonBody)
            }

            Log.d(
                "LOGIN_OK",
                "Status = ${response.status.value}"
            )

            response.status.value

        } catch (e: Exception) {

            Log.e(
                "LOGIN_ERROR",
                "Error al conectar",
                e
            )

            0
        }
    }
}