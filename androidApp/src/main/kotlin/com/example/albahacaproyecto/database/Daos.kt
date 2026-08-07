package com.example.albahacaproyecto.database

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {
    @Query("SELECT * FROM recetas_locales")
    fun getAllRecetasFlow(): Flow<List<RecetaEntity>>

    @Query("SELECT * FROM recetas_locales")
    suspend fun getAllRecetas(): List<RecetaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceta(receta: RecetaEntity)

    @Query("DELETE FROM recetas_locales WHERE localId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM recetas_locales WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): RecetaEntity?

    @Query("SELECT * FROM recetas_locales WHERE sincronizado = 0")
    suspend fun getNoSincronizados(): List<RecetaEntity>

    @Query("DELETE FROM recetas_locales WHERE sincronizado = 1")
    suspend fun clearSynchronized()
}

@Dao
interface FrutaDao {
    @Query("SELECT * FROM frutas_locales")
    fun getAllFrutasFlow(): Flow<List<FrutaEntity>>

    @Query("SELECT * FROM frutas_locales")
    suspend fun getAllFrutas(): List<FrutaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFruta(fruta: FrutaEntity)

    @Query("DELETE FROM frutas_locales WHERE localId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM frutas_locales WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): FrutaEntity?

    @Query("SELECT * FROM frutas_locales WHERE sincronizado = 0")
    suspend fun getNoSincronizados(): List<FrutaEntity>

    @Query("DELETE FROM frutas_locales WHERE sincronizado = 1")
    suspend fun clearSynchronized()
}

@Dao
interface CompraDao {
    @Query("SELECT * FROM compras_locales")
    fun getAllComprasFlow(): Flow<List<CompraEntity>>

    @Query("SELECT * FROM compras_locales")
    suspend fun getAllCompras(): List<CompraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompra(compra: CompraEntity)

    @Query("DELETE FROM compras_locales WHERE localId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM compras_locales WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): CompraEntity?

    @Query("DELETE FROM compras_locales WHERE sincronizado = 1")
    suspend fun clearSynchronized()
}

@Dao
interface ProductoRama2Dao {
    @Query("SELECT * FROM productos_rama2_locales")
    fun getAllFlow(): Flow<List<ProductoRama2Entity>>

    @Query("SELECT * FROM productos_rama2_locales")
    suspend fun getAll(): List<ProductoRama2Entity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: ProductoRama2Entity)

    @Query("DELETE FROM productos_rama2_locales WHERE localId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM productos_rama2_locales WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): ProductoRama2Entity?

    @Query("DELETE FROM productos_rama2_locales WHERE sincronizado = 1")
    suspend fun clearSynchronized()
}
