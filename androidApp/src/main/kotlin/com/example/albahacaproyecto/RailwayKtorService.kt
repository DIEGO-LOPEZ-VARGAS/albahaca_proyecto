package com.example.albahacaproyecto

import io.ktor.client.call.*
import io.ktor.client.request.*

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS LOCALES DE UI
// ─────────────────────────────────────────────────────────────────────────────

/** Registro guardado localmente de cada petición realizada */
data class HistorialEntry(
    val id: Int,
    val timestamp: String,
    val method: String,
    val ruta: String,
    val statusCode: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// SERVICIO HTTP
// ─────────────────────────────────────────────────────────────────────────────

object RailwayKtorService {

    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    /**
     * GET /api/railway/status
     */
    suspend fun obtenerEstado(): RailwayStatusResponse {
        return client.get("$BASE_URL/api/railway/status").body()
    }

    /**
     * GET /api/railway/ping
     */
    suspend fun ping(): Long {
        val inicio = System.currentTimeMillis()
        client.get("$BASE_URL/api/railway/ping")
        return System.currentTimeMillis() - inicio
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL
// ─────────────────────────────────────────────────────────────────────────────

class RailwayRepository {

    companion object {
        private val _historial = mutableListOf<HistorialEntry>()
        private var _nextId = 1
    }

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

    suspend fun obtenerHistorial(): List<HistorialEntry> = _historial.take(15)
}
