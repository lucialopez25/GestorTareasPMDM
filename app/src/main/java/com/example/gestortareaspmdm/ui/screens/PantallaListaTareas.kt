package com.example.gestortareaspmdm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestortareaspmdm.R
import com.example.gestortareaspmdm.data.local.AppDatabase
import com.example.gestortareaspmdm.data.local.Tarea
import com.example.gestortareaspmdm.data.preferences.PreferenciasFiltro
import kotlinx.coroutines.launch

/**
 * Pantalla principal que muestra las tareas guardadas en Room, permite filtrarlas
 * mediante Preferences DataStore y ofrece acciones para crear, editar, eliminar y compartir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaTareas(
    onNuevaTarea: () -> Unit,
    onEditarTarea: (Tarea) -> Unit,
    onIrAEjemplos: () -> Unit,
    onCompartir: (Tarea) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tareaDao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val preferenciasFiltro = remember { PreferenciasFiltro(context) }
    val scope = rememberCoroutineScope()

    val tareas by tareaDao.observarTareas().collectAsStateWithLifecycle(initialValue = emptyList())
    val soloPendientes by preferenciasFiltro.soloPendientesFlow.collectAsStateWithLifecycle(initialValue = false)

    val tareasFiltradas = remember(tareas, soloPendientes) {
        if (soloPendientes) {
            tareas.filter { !it.completada }
        } else {
            tareas
        }
    }

    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_tarea),
                            contentDescription = stringResource(R.string.cd_icono_app),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.titulo_app),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = onIrAEjemplos,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.btn_tareas_ejemplo))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaTarea,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.btn_nueva_tarea)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Filtro persistido en Preferences DataStore
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.filtro_solo_pendientes),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = soloPendientes,
                    onCheckedChange = { activo ->
                        scope.launch {
                            preferenciasFiltro.guardarSoloPendientes(activo)
                        }
                    }
                )
            }

            if (tareasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tareas.isEmpty()) {
                            stringResource(R.string.lista_vacia_sin_tareas)
                        } else {
                            stringResource(R.string.lista_vacia_sin_pendientes)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                ) {
                    items(
                        items = tareasFiltradas,
                        key = { it.id }
                    ) { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            onCambiarCompletada = { marcada ->
                                scope.launch {
                                    tareaDao.actualizar(tarea.copy(completada = marcada))
                                }
                            },
                            onEditar = { onEditarTarea(tarea) },
                            onSolicitarEliminar = { tareaAEliminar = tarea },
                            onCompartir = { onCompartir(tarea) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación antes de eliminar una tarea de Room
    tareaAEliminar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text(text = stringResource(R.string.dialogo_eliminar_titulo)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.dialogo_eliminar_mensaje,
                        tarea.titulo
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            tareaDao.eliminar(tarea)
                            tareaAEliminar = null
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_confirmar_eliminar),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) {
                    Text(text = stringResource(R.string.btn_cancelar))
                }
            }
        )
    }
}

@Composable
private fun TarjetaTarea(
    tarea: Tarea,
    onCambiarCompletada: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onSolicitarEliminar: () -> Unit,
    onCompartir: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onCambiarCompletada
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.etiqueta_prioridad, tarea.prioridad),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_editar)
                    )
                }
                IconButton(onClick = onCompartir) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.cd_compartir)
                    )
                }
                IconButton(onClick = onSolicitarEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_eliminar),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
