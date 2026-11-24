package com.example.gestortareaspmdm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gestortareaspmdm.R
import com.example.gestortareaspmdm.data.local.AppDatabase
import com.example.gestortareaspmdm.data.local.Tarea
import kotlinx.coroutines.launch

/**
 * Formulario reutilizable para crear una tarea nueva (tareaEditar == null)
 * o editar una existente (tareaEditar != null).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioTarea(
    tareaEditar: Tarea?,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    cargandoTarea: Boolean = false
) {
    val context = LocalContext.current
    val tareaDao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val scope = rememberCoroutineScope()

    val opcionesPrioridad = listOf(
        stringResource(R.string.prioridad_baja),
        stringResource(R.string.prioridad_media),
        stringResource(R.string.prioridad_alta)
    )
    val prioridadPorDefecto = stringResource(R.string.prioridad_media)
    val mensajeErrorTitulo = stringResource(R.string.error_titulo_invalido)
    val mensajeErrorPrioridad = stringResource(R.string.error_prioridad_invalida)

    // rememberSaveable conserva los datos del formulario al girar la pantalla
    var titulo by rememberSaveable { mutableStateOf(tareaEditar?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaEditar?.descripcion ?: "") }
    var prioridad by rememberSaveable {
        mutableStateOf(
            tareaEditar?.prioridad?.takeIf { it in opcionesPrioridad } ?: prioridadPorDefecto
        )
    }

    var menuDesplegableAbierto by remember { mutableStateOf(false) }
    var errorTitulo by rememberSaveable { mutableStateOf<String?>(null) }
    var errorPrioridad by rememberSaveable { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (tareaEditar == null) {
                            stringResource(R.string.titulo_nueva_tarea)
                        } else {
                            stringResource(R.string.titulo_editar_tarea)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    if (errorTitulo != null) errorTitulo = null
                },
                label = { Text(text = stringResource(R.string.campo_titulo)) },
                singleLine = true,
                isError = errorTitulo != null,
                modifier = Modifier.fillMaxWidth()
            )
            errorTitulo?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(text = stringResource(R.string.campo_descripcion)) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuDesplegableAbierto,
                onExpandedChange = { menuDesplegableAbierto = !menuDesplegableAbierto }
            ) {
                OutlinedTextField(
                    value = prioridad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.campo_prioridad)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuDesplegableAbierto)
                    },
                    isError = errorPrioridad != null,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = menuDesplegableAbierto,
                    onDismissRequest = { menuDesplegableAbierto = false }
                ) {
                    opcionesPrioridad.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(text = opcion) },
                            onClick = {
                                prioridad = opcion
                                errorPrioridad = null
                                menuDesplegableAbierto = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            errorPrioridad?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onVolver,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.btn_cancelar))
                }

                Button(
                    onClick = {
                        val tituloLimpio = titulo.trim()
                        val descripcionLimpia = descripcion.trim()

                        var hayErrores = false
                        if (tituloLimpio.isEmpty() || tituloLimpio.length < 3) {
                            errorTitulo = mensajeErrorTitulo
                            hayErrores = true
                        }
                        if (prioridad !in opcionesPrioridad) {
                            errorPrioridad = mensajeErrorPrioridad
                            hayErrores = true
                        }

                        if (!hayErrores && !guardando) {
                            guardando = true
                            scope.launch {
                                try {
                                    if (tareaEditar == null) {
                                        tareaDao.insertar(
                                            Tarea(
                                                titulo = tituloLimpio,
                                                descripcion = descripcionLimpia,
                                                prioridad = prioridad
                                            )
                                        )
                                    } else {
                                        tareaDao.actualizar(
                                            tareaEditar.copy(
                                                titulo = tituloLimpio,
                                                descripcion = descripcionLimpia,
                                                prioridad = prioridad
                                            )
                                        )
                                    }
                                    onVolver()
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    },
                    enabled = !guardando && !cargandoTarea,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (guardando) {
                            stringResource(R.string.btn_guardando)
                        } else {
                            stringResource(R.string.btn_guardar)
                        }
                    )
                }
            }
        }
    }
}
