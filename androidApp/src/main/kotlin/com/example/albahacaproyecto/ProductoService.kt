package com.example.albahacaproyecto

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// MODELO LOCAL (Para UI)
// ─────────────────────────────────────────────────────────────────────────────

data class ProductoLocal(
    val id: Int,
    val nombre_producto: String,
    val cantidad: Int,
    val fecha_caducidad: String,
    val tipo_almacenamiento: String,
    val disponible: Boolean,
    val guardadoEn: String
)

// ─────────────────────────────────────────────────────────────────────────────
// SERVICIO HTTP
// ─────────────────────────────────────────────────────────────────────────────

object ProductosService {

    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    /** GET /api/rama2/productos */
    suspend fun obtenerProductos(): ProductosResponse {
        return client.get("$BASE_URL/api/rama2/productos").body()
    }

    /** GET /api/rama2/compras */
    suspend fun obtenerCompras(): ProductosResponse {
        return client.get("$BASE_URL/api/rama2/compras").body()
    }

    /** POST /api/rama2/compras */
    suspend fun agregarCompra(producto: Producto): String {
        return client.post("$BASE_URL/api/rama2/compras") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(producto)
        }.bodyAsText()
    }

    /** PUT /api/rama2/compras/{id} */
    suspend fun actualizarCompra(id: Int, comprado: Boolean): String {
        return client.put("$BASE_URL/api/rama2/compras/$id") {
            parameter("comprado", comprado)
        }.bodyAsText()
    }

    /** DELETE /api/rama2/compras/{id} */
    suspend fun eliminarCompra(id: Int): String {
        return client.delete("$BASE_URL/api/rama2/compras/$id").bodyAsText()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL
// ─────────────────────────────────────────────────────────────────────────────

class ProductosRepository {

    companion object {
        private val _productosGuardados = mutableListOf<ProductoLocal>()
    }

    suspend fun guardarProductos(productos: List<Producto>) {
        _productosGuardados.clear()
        val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        productos.forEach { p ->
            _productosGuardados.add(
                ProductoLocal(
                    id                  = p.id,
                    nombre_producto     = p.nombre_producto,
                    cantidad            = p.cantidad,
                    fecha_caducidad     = p.fecha_caducidad,
                    tipo_almacenamiento = p.tipo_almacenamiento,
                    disponible          = p.disponible,
                    guardadoEn          = hora
                )
            )
        }
    }

    suspend fun obtenerProductosLocales(): List<ProductoLocal> = _productosGuardados.toList()
}
