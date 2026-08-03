package com.example.albahacaproyecto.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas_locales")
data class RecetaEntity(
    @PrimaryKey(autoGenerate = true) var localId: Int = 0,
    val remoteId: Int = 0,
    val titulo: String = "",
    val ingredientes: String = "",
    val pasos: String = "",
    val sincronizado: Boolean = true
)

@Entity(tableName = "frutas_locales")
data class FrutaEntity(
    @PrimaryKey(autoGenerate = true) var localId: Int = 0,
    val remoteId: Int = 0,
    val nombre: String = "",
    val cantidad: Int = 0,
    val fechaCaducidad: String = "",
    val lugarAlmacenamiento: String = "",
    val sincronizado: Boolean = true
)
