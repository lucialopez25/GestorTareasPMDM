package com.example.gestortareaspmdm.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única de Preferences DataStore asociada al Context de la aplicación.
private val Context.dataStore by preferencesDataStore(name = "ajustes_tareas")

/**
 * Gestiona la preferencia booleana del filtro "Mostrar solo pendientes"
 * usando Preferences DataStore.
 */
class PreferenciasFiltro(private val context: Context) {

    companion object {
        private val CLAVE_SOLO_PENDIENTES = booleanPreferencesKey("solo_pendientes")
    }

    /**
     * Flujo reactivo que emite si está activo el filtro de solo pendientes.
     * Por defecto devuelve false para mostrar todas las tareas inicialmente.
     */
    val soloPendientesFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_SOLO_PENDIENTES] ?: false
    }

    /**
     * Guarda de forma asíncrona el nuevo estado del filtro en DataStore.
     */
    suspend fun guardarSoloPendientes(soloPendientes: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CLAVE_SOLO_PENDIENTES] = soloPendientes
        }
    }
}
