package com.example.gestortareaspmdm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestortareaspmdm.data.local.AppDatabase
import com.example.gestortareaspmdm.data.local.Tarea
import com.example.gestortareaspmdm.ui.screens.PantallaFormularioTarea
import com.example.gestortareaspmdm.ui.screens.PantallaListaTareas
import com.example.gestortareaspmdm.ui.screens.PantallaTareasEjemplo
import com.example.gestortareaspmdm.ui.theme.GestorTareasPMDMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorTareasPMDMTheme {
                GestorTareasApp()
            }
        }
    }
}

@Composable
fun GestorTareasApp() {
    val context = LocalContext.current
    val tareaDao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val tareas by tareaDao.observarTareas().collectAsStateWithLifecycle(initialValue = emptyList())

    // Navegación sencilla mediante estados guardados ante rotaciones de pantalla
    var pantallaActual by rememberSaveable { mutableStateOf("lista") }
    var tareaIdEditar by rememberSaveable { mutableStateOf<Int?>(null) }

    val tareaEditar = remember(tareas, tareaIdEditar) {
        tareaIdEditar?.let { id -> tareas.find { it.id == id } }
    }

    when (pantallaActual) {
        "lista" -> {
            PantallaListaTareas(
                onNuevaTarea = {
                    tareaIdEditar = null
                    pantallaActual = "formulario"
                },
                onEditarTarea = { tarea ->
                    tareaIdEditar = tarea.id
                    pantallaActual = "formulario"
                },
                onIrAEjemplos = {
                    pantallaActual = "ejemplos"
                },
                onCompartir = { tarea ->
                    compartirTarea(context, tarea)
                }
            )
        }

        "formulario" -> {
            PantallaFormularioTarea(
                tareaEditar = tareaEditar,
                cargandoTarea = tareaIdEditar != null && tareaEditar == null,
                onVolver = {
                    tareaIdEditar = null
                    pantallaActual = "lista"
                }
            )
        }

        "ejemplos" -> {
            PantallaTareasEjemplo(
                onVolver = {
                    pantallaActual = "lista"
                }
            )
        }
    }
}

private fun compartirTarea(context: Context, tarea: Tarea) {
    val descripcionTexto = tarea.descripcion.ifBlank {
        context.getString(R.string.compartir_sin_descripcion)
    }
    val textoTarea = context.getString(
        R.string.compartir_plantilla_texto,
        tarea.titulo,
        descripcionTexto,
        tarea.prioridad
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textoTarea)
    }

    try {
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.compartir_titulo_selector))
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.compartir_error_sin_app),
            Toast.LENGTH_SHORT
        ).show()
    }
}
