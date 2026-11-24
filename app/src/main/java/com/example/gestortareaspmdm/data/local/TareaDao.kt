package com.example.gestortareaspmdm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) con las operaciones CRUD sobre la tabla "tareas".
 */
@Dao
interface TareaDao {

    /**
     * Observa en tiempo real todas las tareas ordenadas de la más reciente a la más antigua.
     * Al devolver un Flow, Room emite automáticamente la nueva lista ante cualquier cambio.
     */
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarTareas(): Flow<List<Tarea>>

    /**
     * Inserta una nueva tarea en la base de datos de forma asíncrona.
     */
    @Insert
    suspend fun insertar(tarea: Tarea)

    /**
     * Actualiza una tarea existente (por ejemplo, su texto o estado de completada).
     */
    @Update
    suspend fun actualizar(tarea: Tarea)

    /**
     * Elimina una tarea concreta de la base de datos.
     */
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
