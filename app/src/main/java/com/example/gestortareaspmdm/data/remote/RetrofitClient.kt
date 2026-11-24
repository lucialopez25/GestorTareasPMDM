package com.example.gestortareaspmdm.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton encargado de construir y exponer el cliente Retrofit de la aplicación.
 */
object RetrofitClient {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * Instancia perezosa (lazy) del servicio de la API lista para realizar peticiones.
     */
    val apiService: TareasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TareasApiService::class.java)
    }
}
