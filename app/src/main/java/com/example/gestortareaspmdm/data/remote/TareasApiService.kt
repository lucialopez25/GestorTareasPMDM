package com.example.gestortareaspmdm.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define los endpoints remotos para consultar tareas de ejemplo.
 */
interface TareasApiService {

    /**
     * Obtiene la lista de tareas de ejemplo filtradas por usuario desde el endpoint "todos".
     * Al ser suspend, Retrofit la ejecuta de forma asíncrona sin bloquear el hilo principal.
     */
    @GET("todos")
    suspend fun obtenerTareasEjemplo(@Query("userId") userId: Int = 1): List<TareaRemota>
}
