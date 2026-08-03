package com.example.albahacaproyecto

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS
// ─────────────────────────────────────────────────────────────────────────────

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
    private val client = KtorClient.client

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
