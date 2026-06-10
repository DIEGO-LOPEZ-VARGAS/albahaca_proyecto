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
// MODELOS
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class Producto(
    val id: Int,
    val nombre_producto: String,
    val cantidad: Int,
    val fecha_caducidad: String,
    val tipo_almacenamiento: String,
    val disponible: Boolean
)

@Serializable
data class ProductosResponse(
    val rama: String,
    val total: Int,
    val productos: List<Producto>
)

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

    private val BASE_URL = "http://192.168.1.70:8080"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /** GET /api/rama2/productos */
    suspend fun obtenerProductos(): ProductosResponse {
        return client.get("$BASE_URL/api/rama2/productos").body()
    }


    suspend fun obtenerCompras(): ProductosResponse {
        return client.get("$BASE_URL/api/rama2/compras").body()
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