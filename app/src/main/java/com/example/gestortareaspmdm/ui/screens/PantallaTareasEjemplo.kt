package com.example.gestortareaspmdm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestortareaspmdm.R
import com.example.gestortareaspmdm.data.remote.RetrofitClient
import com.example.gestortareaspmdm.data.remote.TareaRemota
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private sealed interface EstadoCargaEjemplos {
    data object Reposo : EstadoCargaEjemplos
    data object Cargando : EstadoCargaEjemplos
    data class Exito(val tareas: List<TareaRemota>) : EstadoCargaEjemplos
    data object Error : EstadoCargaEjemplos
}

/**
 * Pantalla de solo lectura para consultar tareas de ejemplo desde la API REST
 * mediante Retrofit, diferenciándolas de las tareas personales almacenadas en Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareasEjemplo(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var estado by remember { mutableStateOf<EstadoCargaEjemplos>(EstadoCargaEjemplos.Reposo) }

    val cargarTareasEjemplo = {
        if (estado !is EstadoCargaEjemplos.Cargando) {
            estado = EstadoCargaEjemplos.Cargando
            scope.launch {
                estado = try {
                    val resultado = RetrofitClient.apiService.obtenerTareasEjemplo(userId = 1)
                    EstadoCargaEjemplos.Exito(resultado)
                } catch (e: CancellationException) {
                    // Respetamos la cancelación de corrutinas sin tratarla como error de red
                    throw e
                } catch (_: Exception) {
                    EstadoCargaEjemplos.Error
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.titulo_tareas_ejemplo),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    OutlinedButton(
                        onClick = onVolver,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.btn_volver))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.subtitulo_tareas_ejemplo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Button(
                onClick = cargarTareasEjemplo,
                enabled = estado !is EstadoCargaEjemplos.Cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.btn_cargar_ejemplos))
            }

            when (val estadoActual = estado) {
                EstadoCargaEjemplos.Reposo -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ejemplos_estado_reposo),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                EstadoCargaEjemplos.Cargando -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.ejemplos_estado_cargando),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is EstadoCargaEjemplos.Exito -> {
                    if (estadoActual.tareas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.ejemplos_estado_vacio),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(
                                items = estadoActual.tareas,
                                key = { it.id }
                            ) { tareaRemota ->
                                TarjetaTareaRemota(tareaRemota = tareaRemota)
                            }
                        }
                    }
                }

                EstadoCargaEjemplos.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.ejemplos_estado_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = cargarTareasEjemplo) {
                            Text(text = stringResource(R.string.btn_reintentar))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTareaRemota(tareaRemota: TareaRemota) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.ejemplos_id, tareaRemota.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (tareaRemota.completed) {
                        stringResource(R.string.estado_completada)
                    } else {
                        stringResource(R.string.estado_pendiente)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (tareaRemota.completed) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            }

            Text(
                text = tareaRemota.title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
