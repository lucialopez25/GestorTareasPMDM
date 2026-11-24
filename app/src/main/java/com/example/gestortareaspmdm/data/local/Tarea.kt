package com.example.gestortareaspmdm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa una fila en la tabla "tareas" de SQLite.
 *
 * @property id Identificador único autogenerado por Room.
 * @property titulo Título corto de la tarea.
 * @property descripcion Detalle o notas adicionales de la tarea.
 * @property prioridad Nivel de prioridad ("Baja", "Media" o "Alta").
 * @property completada Indica si la tarea ha sido marcada como terminada.
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val completada: Boolean = false
)
