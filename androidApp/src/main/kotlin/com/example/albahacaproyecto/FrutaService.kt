package com.example.albahacaproyecto

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.client.statement.*

class FrutaApiClient {
    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    suspend fun enviarFruta(fruta: Fruta): Result<Fruta> {
        return try {
            val response = client.post("$BASE_URL/api/frutas") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(fruta)
            }
            if (response.status.value == 401) {
                Result.failure(Exception("401"))
            } else if (response.status.value in 200..299) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerFrutas(): Result<List<Fruta>> {
        return try {
            val response = client.get("$BASE_URL/api/frutas") {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            if (response.status.value == 401) {
                Result.failure(Exception("401"))
            } else {
                Result.success(response.body())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarFruta(id: Int): Result<Boolean> {
        return try {
            val response = client.delete("$BASE_URL/api/frutas/$id") {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            if (response.status.value == 200) Result.success(true)
            else Result.failure(Exception("Error ${response.status.value}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarFruta(id: Int, fruta: Fruta): Result<Boolean> {
        return try {
            val response = client.put("$BASE_URL/api/frutas/$id") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(fruta)
            }
            if (response.status.value == 200) Result.success(true)
            else Result.failure(Exception("Error ${response.status.value}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analizarImagen(imageBytes: ByteArray): Result<List<Fruta>> {
        return try {
            val response = client.post("$BASE_URL/api/inventario/vision") {
                // Timeout extendido para IA Vision
                timeout {
                    requestTimeoutMillis = 90000
                }
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"fruta.jpg\"")
                        })
                    }
                ))
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error vision ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

object LocalStorage {
    var ultimaFruta: Fruta? = null
}
