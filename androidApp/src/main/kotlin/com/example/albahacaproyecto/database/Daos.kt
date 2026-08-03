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

    @Query("SELECT * FROM frutas_locales WHERE sincronizado = 0")
    suspend fun getNoSincronizados(): List<FrutaEntity>

    @Query("DELETE FROM frutas_locales WHERE sincronizado = 1")
    suspend fun clearSynchronized()
}
