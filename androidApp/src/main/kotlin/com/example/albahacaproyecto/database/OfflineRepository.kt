package com.example.albahacaproyecto.database

import android.content.Context
import com.example.albahacaproyecto.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import io.ktor.client.request.*
import io.ktor.http.*

class OfflineRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val recetaDao = db.recetaDao()
    private val frutaDao = db.frutaDao()
    private val compraDao = db.compraDao()
    private val productoRama2Dao = db.productoRama2Dao()
    
    private val recetaApi = RecetaApiClient()
    private val frutaApi = FrutaApiClient()
    private val productosApi = ProductosService

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // --- RECETAS ---
    fun getRecetasFlow(): Flow<List<Receta>> {
        repositoryScope.launch {
            try {
                recetaApi.obtenerRecetas().onSuccess { cloudList ->
                    cloudList.forEach { r ->
                        val existing = recetaDao.getByRemoteId(r.id)
                        val entity = RecetaEntity(
                            localId = existing?.localId ?: 0,
                            remoteId = r.id,
                            titulo = r.titulo,
                            ingredientes = r.ingredientes,
                            pasos = r.pasos,
                            sincronizado = true
                        )
                        recetaDao.insertReceta(entity)
                    }
                }
            } catch (e: Exception) { }
        }
        
        return recetaDao.getAllRecetasFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun guardarReceta(r: Receta): Result<Boolean> {
        val res = recetaApi.enviarReceta(r)
        val entity = RecetaEntity(
            localId = r.localId,
            remoteId = r.id,
            titulo = r.titulo,
            ingredientes = r.ingredientes,
            pasos = r.pasos,
            sincronizado = res.isSuccess
        )
        recetaDao.insertReceta(entity)
        return res
    }

    suspend fun eliminarReceta(localId: Int, remoteId: Int): Result<Boolean> {
        recetaDao.deleteById(localId)
        return if (remoteId != 0) recetaApi.eliminarReceta(remoteId) else Result.success(true)
    }

    suspend fun actualizarReceta(r: Receta): Result<Boolean> {
        val res = recetaApi.actualizarReceta(r.id, r)
        val entity = RecetaEntity(
            localId = r.localId,
            remoteId = r.id,
            titulo = r.titulo,
            ingredientes = r.ingredientes,
            pasos = r.pasos,
            sincronizado = res.isSuccess
        )
        recetaDao.insertReceta(entity)
        return res
    }

    // --- FRUTAS ---
    fun getFrutasFlow(): Flow<List<Fruta>> {
        repositoryScope.launch {
            try {
                frutaApi.obtenerFrutas().onSuccess { cloudList ->
                    cloudList.forEach { f ->
                        val existing = frutaDao.getByRemoteId(f.id)
                        val entity = FrutaEntity(
                            localId = existing?.localId ?: 0,
                            remoteId = f.id,
                            nombre = f.nombre,
                            cantidad = f.cantidad,
                            fechaCaducidad = f.fechaCaducidad,
                            lugarAlmacenamiento = f.lugarAlmacenamiento,
                            sincronizado = true
                        )
                        frutaDao.insertFruta(entity)
                    }
                }
            } catch (e: Exception) { }
        }
        
        return frutaDao.getAllFrutasFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getFrutas(): List<Fruta> {
        return frutaDao.getAllFrutas().map { it.toDomain() }
    }

    suspend fun guardarFruta(f: Fruta): Result<Boolean> {
        val res = frutaApi.enviarFruta(f)
        val entity = FrutaEntity(
            localId = f.localId,
            remoteId = f.id,
            nombre = f.nombre,
            cantidad = f.cantidad,
            fechaCaducidad = f.fechaCaducidad,
            lugarAlmacenamiento = f.lugarAlmacenamiento,
            sincronizado = res.isSuccess
        )
        frutaDao.insertFruta(entity)
        return res
    }

    suspend fun eliminarFruta(localId: Int, remoteId: Int): Result<Boolean> {
        frutaDao.deleteById(localId)
        return if (remoteId != 0) frutaApi.eliminarFruta(remoteId) else Result.success(true)
    }

    suspend fun actualizarFruta(f: Fruta): Result<Boolean> {
        val res = frutaApi.actualizarFruta(f.id, f)
        val entity = FrutaEntity(
            localId = f.localId,
            remoteId = f.id,
            nombre = f.nombre,
            cantidad = f.cantidad,
            fechaCaducidad = f.fechaCaducidad,
            lugarAlmacenamiento = f.lugarAlmacenamiento,
            sincronizado = res.isSuccess
        )
        frutaDao.insertFruta(entity)
        return res
    }

    // --- COMPRAS ---
    fun getComprasFlow(): Flow<List<Fruta>> {
        repositoryScope.launch {
            try {
                val res = productosApi.obtenerCompras()
                res.productos.forEach { p ->
                    val existing = compraDao.getByRemoteId(p.id)
                    compraDao.insertCompra(CompraEntity(
                        localId = existing?.localId ?: 0,
                        remoteId = p.id,
                        nombreProducto = p.nombreProducto,
                        cantidad = p.cantidad,
                        fechaCaducidad = p.fechaCaducidad,
                        tipoAlmacenamiento = p.tipoAlmacenamiento,
                        comprado = !p.disponible,
                        sincronizado = true
                    ))
                }
            } catch (e: Exception) { }
        }
        return compraDao.getAllComprasFlow().map { entities ->
            entities.map { Fruta(it.remoteId, it.localId, it.nombreProducto, it.cantidad, it.fechaCaducidad, it.tipoAlmacenamiento) }
        }
    }

    suspend fun guardarCompra(f: Fruta): Result<Boolean> {
        val p = Producto(
            id = f.id,
            nombreProducto = f.nombre,
            cantidad = f.cantidad,
            disponible = true,
            fechaCaducidad = f.fechaCaducidad,
            tipoAlmacenamiento = f.lugarAlmacenamiento
        )
        val res = try {
            val response = KtorClient.client.post("${KtorClient.BASE_URL}/api/compras") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(p)
            }
            Result.success(response.status.value in 200..299)
        } catch (e: Exception) {
            Result.failure(e)
        }

        compraDao.insertCompra(CompraEntity(
            localId = f.localId,
            remoteId = f.id,
            nombreProducto = f.nombre,
            cantidad = f.cantidad,
            fechaCaducidad = f.fechaCaducidad,
            tipoAlmacenamiento = f.lugarAlmacenamiento,
            sincronizado = res.isSuccess
        ))
        return res
    }

    suspend fun eliminarCompra(localId: Int, remoteId: Int): Result<Boolean> {
        compraDao.deleteById(localId)
        return if (remoteId != 0) {
            try {
                val response = KtorClient.client.delete("${KtorClient.BASE_URL}/api/compras/$remoteId") {
                    KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                }
                Result.success(response.status.value in 200..299)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else Result.success(true)
    }

    // --- RAMA 2 ---
    fun getProductosRama2Flow(): Flow<List<ProductoLocal>> {
        repositoryScope.launch {
            try {
                val res = productosApi.obtenerProductos()
                res.productos.forEach { p ->
                    val existing = productoRama2Dao.getByRemoteId(p.id)
                    productoRama2Dao.insert(ProductoRama2Entity(
                        localId = existing?.localId ?: 0,
                        remoteId = p.id,
                        nombreProducto = p.nombreProducto,
                        cantidad = p.cantidad,
                        fechaCaducidad = p.fechaCaducidad,
                        tipoAlmacenamiento = p.tipoAlmacenamiento,
                        disponible = p.disponible,
                        sincronizado = true
                    ))
                }
            } catch (e: Exception) { }
        }
        return productoRama2Dao.getAllFlow().map { entities ->
            entities.map { 
                ProductoLocal(it.remoteId, it.nombreProducto, it.cantidad, it.fechaCaducidad, it.tipoAlmacenamiento, it.disponible, it.guardadoEn) 
            }
        }
    }
}

// Extensiones para mapeo
fun RecetaEntity.toDomain() = Receta(remoteId, localId, titulo, ingredientes, pasos)
fun FrutaEntity.toDomain() = Fruta(remoteId, localId, nombre, cantidad, fechaCaducidad, lugarAlmacenamiento)
