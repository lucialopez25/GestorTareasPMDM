package com.example.gestortareaspmdm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de Room para la aplicación.
 * Define las entidades registradas y la versión actual del esquema.
 */
@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Devuelve la implementación generada por Room del DAO de tareas.
     */
    abstract fun tareaDao(): TareaDao

    companion object {
        // @Volatile asegura que el valor de INSTANCE sea visible inmediatamente para todos los hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única (Singleton) de la base de datos.
         * Si aún no existe, la crea de forma segura entre hilos usando el contexto de aplicación.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tareas_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
