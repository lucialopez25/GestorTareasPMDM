package com.example.gestortareaspmdm.data.remote

/**
 * Modelo de datos para mapear cada tarea recibida desde la API REST de JSONPlaceholder.
 *
 * @property id Identificador remoto de la tarea.
 * @property title Título de la tarea devuelto por el servidor.
 * @property completed Indica si la tarea figura como completada en la API.
 */
data class TareaRemota(
    val id: Int,
    val title: String,
    val completed: Boolean
)
