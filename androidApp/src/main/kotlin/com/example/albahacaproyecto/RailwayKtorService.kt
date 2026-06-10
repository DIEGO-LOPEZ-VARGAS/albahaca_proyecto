package com.example.albahacaproyecto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS  (comparten package con el resto del proyecto)
// ─────────────────────────────────────────────────────────────────────────────

/** Respuesta del end-point GET /api/railway/status */
@Serializable
data class RailwayStatusResponse(
    val online: Boolean,
    val serverUrl: String,
    val latencyMs: Long,
    val routes: List<RouteInfo>
)

/** Información de una ruta registrada en el servidor Ktor */
@Serializable
data class RouteInfo(
    val method: String,
    val path: String,
    val description: String
)

/** Registro guardado localmente de cada petición realizada */
data class HistorialEntry(
    val id: Int,
    val timestamp: String,
    val method: String,
    val ruta: String,
    val statusCode: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// SERVICIO HTTP  –  extiende la misma estrategia de KtorClient.kt existente
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Consumo del end-point de la Rama 3.
 *
 * Usa el mismo engine Android y la misma configuración JSON que KtorClient,
 * pero apunta a las rutas específicas de Railway.
 *
 * Base URL: http://10.0.2.2:8080  (emulador → localhost del PC)
 * En producción Railway sería: https://tu-app.up.railway.app
 */
object RailwayKtorService {

    private val BASE_URL = "http://192.168.1.70:8080"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /**
     * GET /api/railway/status
     * Devuelve el estado del servidor y la lista de rutas registradas.
     */
    suspend fun obtenerEstado(): RailwayStatusResponse {
        return client.get("$BASE_URL/api/railway/status").body()
    }

    /**
     * GET /api/railway/ping
     * Respuesta mínima para medir latencia.
     */
    suspend fun ping(): Long {
        val inicio = System.currentTimeMillis()
        client.get("$BASE_URL/api/railway/ping")
        return System.currentTimeMillis() - inicio
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL  –  almacenamiento en memoria (reemplazable por SQLDelight)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Persiste el historial de peticiones HTTP realizadas al servidor.
 *
 * Actualmente usa una lista en memoria para no añadir dependencias nuevas
 * al proyecto. Para persistencia real entre sesiones, reemplaza la lista
 * por SQLDelight (ver AppDatabase.sq adjunto).
 */
class RailwayRepository {

    companion object {
        // Singleton compartido para que el historial sobreviva recomposiciones
        private val _historial = mutableListOf<HistorialEntry>()
        private var _nextId = 1
    }

    /** Guarda un registro de la petición realizada */
    suspend fun guardarPeticion(method: String, ruta: String, statusCode: Int) {
        val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        _historial.add(
            0,
            HistorialEntry(
                id         = _nextId++,
                timestamp  = hora,
                method     = method,
                ruta       = ruta,
                statusCode = statusCode
            )
        )
    }

    /** Retorna los últimos 15 registros del historial */
    suspend fun obtenerHistorial(): List<HistorialEntry> = _historial.take(15)
}