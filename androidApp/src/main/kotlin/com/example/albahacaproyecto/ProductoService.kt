package com.example.albahacaproyecto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class Producto(
    val id: Int,
    @SerialName("nombre_producto") val nombreProducto: String,
    val cantidad: Int,
    @SerialName("fecha_caducidad") val fechaCaducidad: String,
    @SerialName("tipo_almacenamiento") val tipoAlmacenamiento: String,
    val disponible: Boolean,
)

@Serializable
data class ProductosResponse(
    val rama: String,
    val total: Int,
    val productos: List<Producto>,
)

data class ProductoLocal(
    val id: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val fechaCaducidad: String,
    val tipoAlmacenamiento: String,
    val disponible: Boolean,
    val guardadoEn: String,
)

// ─────────────────────────────────────────────────────────────────────────────
// SERVICIO HTTP
// ─────────────────────────────────────────────────────────────────────────────

object ProductosService {

    private const val BASE_URL = KtorClient.BASE_URL

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /** GET /api/compras */
    suspend fun obtenerProductos(): ProductosResponse {
        return client.get("$BASE_URL/api/compras") {
            KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    /** GET /api/compras */
    suspend fun obtenerCompras(): ProductosResponse {
        return client.get("$BASE_URL/api/compras") {
            KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL (EN MEMORIA)
// ─────────────────────────────────────────────────────────────────────────────

class ProductosRepository {

    companion object {
        private val _productosGuardados = mutableListOf<ProductoLocal>()
    }

    fun guardarProductos(productos: List<Producto>) {
        _productosGuardados.clear()
        val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        productos.forEach { p ->
            _productosGuardados.add(
                ProductoLocal(
                    id                  = p.id,
                    nombreProducto      = p.nombreProducto,
                    cantidad            = p.cantidad,
                    fechaCaducidad      = p.fechaCaducidad,
                    tipoAlmacenamiento  = p.tipoAlmacenamiento,
                    disponible          = p.disponible,
                    guardadoEn          = hora
                )
            )
        }
    }

    fun obtenerProductosLocales(): List<ProductoLocal> = _productosGuardados.toList()
}
