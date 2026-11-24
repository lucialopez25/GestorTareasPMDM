# Gestor Personal de Tareas (PMDM)

Aplicación móvil nativa para Android desarrollada en **Kotlin** con **Jetpack Compose** y **Material Design 3**. Permite gestionar tareas personales de forma local, mantener preferencias de filtrado reactivo, consultar tareas de ejemplo desde una API REST externa y compartir tareas mediante aplicaciones del sistema.

---

## 📱 Características Principales

* **Gestión completa de tareas (CRUD):** Creación, listado, edición, marcado como completada y borrado seguro con diálogo de confirmación (`AlertDialog`).
* **Validación de formularios:** Comprobación de título obligatorio (mínimo 3 caracteres), descripción opcional, selector de prioridad (*Baja*, *Media*, *Alta*) y bloqueo de envíos duplicados.
* **Filtro persistente:** Interruptor *«Mostrar solo pendientes»* cuya preferencia se conserva entre sesiones.
* **Consulta de API REST externa:** Apartado independiente *«Tareas de ejemplo»* conectado a JSONPlaceholder mediante Retrofit, con gestión de estados (reposo, carga, éxito, vacío y error con reintento) sin mezclar datos con la base de datos local.
* **Compartición con el sistema:** Envío del resumen formateado de cada tarea hacia otras aplicaciones instaladas mediante `Intent.ACTION_SEND`.

---

## 🛠️ Stack Tecnológico y Arquitectura

El proyecto implementa una arquitectura limpia con una única `MainActivity`, navegación gestionada por estados (`rememberSaveable`) y separación entre la capa de datos y la interfaz de usuario:

* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose + Material 3 (`Scaffold`, `LazyColumn`, `ExposedDropdownMenuBox`, `AlertDialog`, `Switch`, `CircularProgressIndicator`)
* **Base de Datos Local:** Room (`@Entity`, `@Dao`, `RoomDatabase` con patrón Singleton y `Flow<List<Tarea>>` reactivo)
* **Preferencias:** Jetpack Preferences DataStore (`Flow<Boolean>`)
* **Red / API REST:** Retrofit 2 + Gson Converter + Corrutinas de Kotlin (`suspend`)
* **Conectividad Android:** Intents implícitos (`Intent.ACTION_SEND` + `Intent.createChooser`)

---
## 🤖 Evidencia de uso de IA
* **Conversación con Gemini:** [Ver historial completo de desarrollo](https://gemini.google.com/share/d/1RAdPQenJAXB6M03MB5R4uGLZaaeXE68L?usp=sharing)

---

## 📂 Estructura del Proyecto

```text
GestorTareasPMDM/
├── app/src/main/
│   ├── java/com/example/gestortareaspmdm/
│   │   ├── data/
│   │   │   ├── local/          # Entidad Tarea, TareaDao y AppDatabase (Room)
│   │   │   ├── preferences/    # PreferenciasFiltro (Preferences DataStore)
│   │   │   └── remote/         # TareaRemota, TareasApiService y RetrofitClient (Retrofit)
│   │   ├── ui/                 # Pantallas Compose (Listado, Formulario y Tareas de ejemplo)
│   │   └── MainActivity.kt     # Punto de entrada, navegación e Intent de compartir
│   ├── res/values/strings.xml  # Recursos de texto externalizados
│   └── AndroidManifest.xml     # Permiso INTERNET y configuración de actividad
├── capturas/                   # Capturas de pantalla de la aplicación
├── app-debug.apk               # Ejecutable generado para instalación directa
└── README.md                   # Documentación del proyecto

