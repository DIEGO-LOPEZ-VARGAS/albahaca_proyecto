package com.example.albahacaproyecto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RecetaEntity::class, FrutaEntity::class, CompraEntity::class, ProductoRama2Entity::class], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun frutaDao(): FrutaDao
    abstract fun compraDao(): CompraDao
    abstract fun productoRama2Dao(): ProductoRama2Dao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "albahaca_db"
                )
                .fallbackToDestructiveMigration() // Para facilitar el desarrollo v3.6
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
