package com.example.albahacaproyecto.database

import android.content.Context
import com.example.albahacaproyecto.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OfflineRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val recetaDao = db.recetaDao()
    private val frutaDao = db.frutaDao()
    
    private val recetaApi = RecetaApiClient()
    private val frutaApi = FrutaApiClient()

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // --- RECETAS ---
    fun getRecetasFlow(): Flow<List<Receta>> {
        repositoryScope.launch {
            try {
                recetaApi.obtenerRecetas().onSuccess { cloudList ->
                    recetaDao.clearSynchronized()
                    cloudList.forEach { r ->
                        recetaDao.insertReceta(RecetaEntity(
                            remoteId = r.id,
                            titulo = r.titulo,
                            ingredientes = r.ingredientes,
                            pasos = r.pasos,
                            sincronizado = true
                        ))
                    }
                }
            } catch (e: Exception) { }
        }
        
        return recetaDao.getAllRecetasFlow().map { list ->
            list.map { Receta(it.remoteId, it.localId, it.titulo, it.ingredientes, it.pasos) }
        }
    }

    suspend fun getRecetas(): List<Receta> {
        return recetaDao.getAllRecetas().map { 
            Receta(it.remoteId, it.localId, it.titulo, it.ingredientes, it.pasos)
        }
    }

    suspend fun guardarReceta(r: Receta): Result<Boolean> {
        val res = recetaApi.enviarReceta(r)
        val entity = RecetaEntity(
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
        recetaDao.insertReceta(RecetaEntity(
            localId = r.localId,
            remoteId = r.id,
            titulo = r.titulo,
            ingredientes = r.ingredientes,
            pasos = r.pasos,
            sincronizado = res.isSuccess
        ))
        return res
    }

    // --- FRUTAS ---
    fun getFrutasFlow(): Flow<List<Fruta>> {
        repositoryScope.launch {
            try {
                frutaApi.obtenerFrutas().onSuccess { cloudList ->
                    frutaDao.clearSynchronized()
                    cloudList.forEach { f ->
                        frutaDao.insertFruta(FrutaEntity(
                            remoteId = f.id,
                            nombre = f.nombre,
                            cantidad = f.cantidad,
                            fechaCaducidad = f.fechaCaducidad,
                            lugarAlmacenamiento = f.lugarAlmacenamiento,
                            sincronizado = true
                        ))
                    }
                }
            } catch (e: Exception) { }
        }
        return frutaDao.getAllFrutasFlow().map { list ->
            list.map { Fruta(it.remoteId, it.localId, it.nombre, it.cantidad, it.fechaCaducidad, it.lugarAlmacenamiento) }
        }
    }

    suspend fun getFrutas(): List<Fruta> {
        return frutaDao.getAllFrutas().map {
            Fruta(it.remoteId, it.localId, it.nombre, it.cantidad, it.fechaCaducidad, it.lugarAlmacenamiento)
        }
    }

    suspend fun guardarFruta(f: Fruta): Result<Boolean> {
        val res = frutaApi.enviarFruta(f)
        frutaDao.insertFruta(FrutaEntity(
            nombre = f.nombre,
            cantidad = f.cantidad,
            fechaCaducidad = f.fechaCaducidad,
            lugarAlmacenamiento = f.lugarAlmacenamiento,
            sincronizado = res.isSuccess
        ))
        return res
    }

    suspend fun eliminarFruta(localId: Int, remoteId: Int): Result<Boolean> {
        frutaDao.deleteById(localId)
        return if (remoteId != 0) frutaApi.eliminarFruta(remoteId) else Result.success(true)
    }

    suspend fun actualizarFruta(f: Fruta): Result<Boolean> {
        val res = frutaApi.actualizarFruta(f.id, f)
        frutaDao.insertFruta(FrutaEntity(
            localId = f.localId,
            remoteId = f.id,
            nombre = f.nombre,
            cantidad = f.cantidad,
            fechaCaducidad = f.fechaCaducidad,
            lugarAlmacenamiento = f.lugarAlmacenamiento,
            sincronizado = res.isSuccess
        ))
        return res
    }
}
