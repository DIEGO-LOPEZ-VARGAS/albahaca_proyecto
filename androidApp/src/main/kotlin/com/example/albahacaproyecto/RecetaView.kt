package com.example.albahacaproyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.albahacaproyecto.database.OfflineRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

class RecetaApiClient {
    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL + "/api/recetas"

    suspend fun enviarReceta(receta: Receta): Result<Boolean> {
        return try {
            val response = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(receta)
            }
            val status = response.status.value
            android.util.Log.d("DEPURACION_ALBAHACA", "Respuesta enviarReceta: $status")
            if (status == 401) {
                Result.failure(Exception("401"))
            } else {
                Result.success(status in 200..299)
            }
        } catch (e: Exception) {
            android.util.Log.e("DEPURACION_ALBAHACA", "Error enviando receta: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun obtenerRecetas(): Result<List<Receta>> {
        return try {
            val response = client.get(BASE_URL) {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            android.util.Log.d("DEPURACION_ALBAHACA", "Respuesta obtenerRecetas: ${response.status.value}")
            if (response.status.value == 401) {
                Result.failure(Exception("401"))
            } else {
                Result.success(response.body())
            }
        } catch (e: Exception) {
            android.util.Log.e("DEPURACION_ALBAHACA", "Error obteniendo recetas: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun generarRecetaIA(ingredientes: List<String>): Result<Receta> {
        return try {
            val response = client.post("$BASE_URL/ia") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(IngredientsRequest(ingredientes))
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<Receta>())
            } else {
                val errorText = response.bodyAsText()
                android.util.Log.e("DEPURACION_ALBAHACA", "Error IA (${response.status.value}): $errorText")
                Result.failure(Exception("Servidor (${response.status.value}): $errorText"))
            }
        } catch (e: Exception) {
            android.util.Log.e("DEPURACION_ALBAHACA", "Falla al generar receta con IA: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun eliminarReceta(id: Int): Result<Boolean> {
        return try {
            val response = client.delete("$BASE_URL/$id") {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            if (response.status == HttpStatusCode.OK) Result.success(true)
            else Result.failure(Exception("Error ${response.status.value}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarReceta(id: Int, receta: Receta): Result<Boolean> {
        return try {
            val response = client.put("$BASE_URL/$id") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(receta)
            }
            if (response.status == HttpStatusCode.OK) Result.success(true)
            else Result.failure(Exception("Error ${response.status.value}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analizarNutricion(receta: Receta): Result<NutricionResponse> {
        return try {
            val response = client.post("$BASE_URL/nutricion") {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(receta)
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<NutricionResponse>())
            } else {
                Result.failure(Exception("Error ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Almacenamiento Local Simple
object RecetaStorage {
    var ultimaRecetaGuardada: Receta? = null
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecetaView() {
    var titulo by remember { mutableStateOf("") }
    var ingredientes by remember { mutableStateOf("") }
    var pasos by remember { mutableStateOf("") }
    
    var isGeneratingIA by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var recetaSeleccionada by remember { mutableStateOf<Receta?>(null) }
    var queryReceta by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val api = remember { RecetaApiClient() }
    val offlineRepo = remember { OfflineRepository(context) }

    val historialRecetas by offlineRepo.getRecetasFlow().collectAsState(initial = emptyList())

    // Inventario disponible
    var listaInventario by remember { mutableStateOf<List<String>>(emptyList()) }
    var seleccionados by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    val VerduritasPrimary = MaterialTheme.colorScheme.primary
    val VerduritasSecondary = MaterialTheme.colorScheme.secondary
    val VerduritasBackground = MaterialTheme.colorScheme.background

    // ... (Launchers and Dialogs remain the same)


    // Diálogo de error
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error de IA") },
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) { Text("Entendido") }
            }
        )
    }

    // Launcher para solicitar permiso de notificaciones (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("NOTIFICACION", "Permiso concedido")
        } else {
            android.util.Log.d("NOTIFICACION", "Permiso denegado")
        }
    }

    // Diálogo para ver el detalle de la receta
    if (recetaSeleccionada != null) {
        RecetaDetalleDialog(
            receta = recetaSeleccionada!!,
            onDismiss = { recetaSeleccionada = null },
            onDelete = { id ->
                scope.launch {
                    val localId = recetaSeleccionada?.localId ?: 0
                    val res = offlineRepo.eliminarReceta(localId, id)
                    res.onSuccess {
                        recetaSeleccionada = null
                    }
                }
            },
            onUpdate = { _, nuevaReceta ->
                scope.launch {
                    val res = offlineRepo.actualizarReceta(nuevaReceta)
                    res.onSuccess {
                        recetaSeleccionada = null
                    }
                }
            }
        )
    }

    // TÉCNICA: Procesos de Notificaciones (Inicialización del Canal y Permisos)
    LaunchedEffect(Unit) {
        crearCanalNotificaciones(context)
        
        // Cargar inventario (Frutas y Productos Rama 2)
        scope.launch {
            try {
                val frutas: List<String> = offlineRepo.getFrutas().map { it.nombre }
                val productos: List<String> = ProductosService.obtenerProductos().productos.map { it.nombreProducto }
                val combinada: List<String> = frutas + productos
                listaInventario = combinada.distinct().filter { it.isNotBlank() }
            } catch (e: Exception) {
                android.util.Log.e("DEPURACION_ALBAHACA", "Error cargando inventario: ${e.message}")
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(VerduritasBackground).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // AI Header
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Recetas", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                    Text("Crea magia con lo que tienes", fontSize = 14.sp, color = Color(0xFF494455))
                }
            }
        }

        // --- SECCIÓN: TU DESPENSA ---
        if (listaInventario.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tus ingredientes disponibles", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VerduritasSecondary)
                    Text("Toca para añadir a la receta", fontSize = 12.sp, color = Color.Gray)
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listaInventario.forEach { item ->
                            val isSelected = seleccionados.contains(item)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        seleccionados = seleccionados - item
                                        // Quitar del texto si estaba
                                        val listaActual = ingredientes.split(",").map { it.trim() }.toMutableList()
                                        listaActual.remove(item)
                                        ingredientes = listaActual.filter { it.isNotBlank() }.joinToString(", ")
                                    } else {
                                        seleccionados = seleccionados + item
                                        // Añadir al texto
                                        val listaActual = ingredientes.split(",").map { it.trim() }.toMutableList()
                                        if (!listaActual.contains(item)) {
                                            listaActual.add(item)
                                        }
                                        ingredientes = listaActual.filter { it.isNotBlank() }.joinToString(", ")
                                    }
                                },
                                label = { Text(item) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerduritasSecondary.copy(alpha = 0.2f),
                                    selectedLabelColor = VerduritasSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Form Section (Previously static chips)

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de la Receta") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = ingredientes,
                    onValueChange = { ingredientes = it },
                    label = { Text("Ingredientes (tomate, albahaca...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = pasos,
                    onValueChange = { pasos = it },
                    label = { Text("Pasos de preparación") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
                Button(
                    onClick = {
                        val nombreReceta = titulo // Guardamos para la notificación
                        
                        // Verificar permiso antes de intentar guardar/notificar en Android 13+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@Button
                            }
                        }

                        val nueva = Receta(titulo = titulo, ingredientes = ingredientes, pasos = pasos)
                        scope.launch {
                            val res: Result<Boolean> = offlineRepo.guardarReceta(nueva)
                            res.onSuccess { exito ->
                                if (exito) {
                                    // TÉCNICA: Procesos de Notificaciones (Disparo del aviso)
                                    enviarNotificacionExito(context, nombreReceta)
                                    
                                    titulo = ""; ingredientes = ""; pasos = ""
                                } else {
                                    android.widget.Toast.makeText(context, "Error al guardar receta en servidor", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }.onFailure { error ->
                                if (error.message == "401") {
                                    errorMessage = "Tu sesión ha expirado. Por favor, cierra sesión e ingresa de nuevo con tu contraseña."
                                    KtorClient.onSessionExpired?.invoke()
                                } else {
                                    // Aun si falló el servidor, la receta se guardó localmente en el Repo
                                    errorMessage = "Guardado localmente (Sin internet). Se sincronizará luego."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerduritasSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GUARDAR RECETA", fontWeight = FontWeight.Bold)
                }
            }
        }

        // AI Generation Button (kept for design)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                color = VerduritasPrimary,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                onClick = {
                    if (isGeneratingIA) return@Surface
                    
                    // Tomar ingredientes del campo de texto
                    val listaIngredientes = ingredientes.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    
                    if (listaIngredientes.isEmpty()) {
                        android.widget.Toast.makeText(context, "Escribe algunos ingredientes primero", android.widget.Toast.LENGTH_SHORT).show()
                        return@Surface
                    }

                    scope.launch {
                        isGeneratingIA = true
                        val resultado = api.generarRecetaIA(listaIngredientes)
                        
                        resultado.onSuccess { recetaGenerada ->
                            titulo = recetaGenerada.titulo
                            ingredientes = recetaGenerada.ingredientes
                            pasos = recetaGenerada.pasos
                            android.widget.Toast.makeText(context, "¡Receta generada!", android.widget.Toast.LENGTH_SHORT).show()
                        }.onFailure { error ->
                            val msg = error.message ?: "Error desconocido"
                            android.util.Log.e("DEPURACION_ALBAHACA", "Error IA: $msg")
                            if (msg.contains("401")) {
                                errorMessage = "Tu sesión ha expirado. Por favor, ingresa de nuevo con tu contraseña."
                                KtorClient.onSessionExpired?.invoke()
                            } else {
                                errorMessage = msg
                            }
                        }
                        
                        isGeneratingIA = false
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isGeneratingIA) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Generar Receta con IA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // My Recipes Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("Mis Recetas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Ver todas", color = VerduritasPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                
                OutlinedTextField(
                    value = queryReceta,
                    onValueChange = { queryReceta = it },
                    label = { Text("Buscar receta...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
            }
        }

        // Historial real de recetas
        val filteredRecetas = historialRecetas.filter { 
            it.titulo.contains(queryReceta, ignoreCase = true) || 
            it.ingredientes.contains(queryReceta, ignoreCase = true) 
        }
        
        items(filteredRecetas) { receta ->
            RecipeCard(
                title = receta.titulo,
                time = "20 min",
                modifier = Modifier.fillMaxWidth(),
                onClick = { recetaSeleccionada = receta }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun RecetaDetalleDialog(
    receta: Receta, 
    onDismiss: () -> Unit, 
    onDelete: (Int) -> Unit,
    onUpdate: (Int, Receta) -> Unit
) {
    var editMode by remember { mutableStateOf(false) }
    var tituloEdit by remember { mutableStateOf(receta.titulo) }
    var ingredientesEdit by remember { mutableStateOf(receta.ingredientes) }
    var pasosEdit by remember { mutableStateOf(receta.pasos) }
    
    var nutricion by remember { mutableStateOf<NutricionResponse?>(null) }
    var isAnalizando by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val api = remember { RecetaApiClient() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (editMode) {
                    OutlinedTextField(
                        value = tituloEdit,
                        onValueChange = { tituloEdit = it },
                        label = { Text("Título") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = receta.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF632CE5),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row {
                    IconButton(onClick = { editMode = !editMode }) {
                        Icon(if (editMode) Icons.Default.Close else Icons.Default.Edit, "Editar", tint = Color.Gray)
                    }
                    IconButton(onClick = { onDelete(receta.id) }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (editMode) {
                    OutlinedTextField(
                        value = ingredientesEdit,
                        onValueChange = { ingredientesEdit = it },
                        label = { Text("Ingredientes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pasosEdit,
                        onValueChange = { pasosEdit = it },
                        label = { Text("Preparación") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                } else {
                    Column {
                        Text("Ingredientes", fontWeight = FontWeight.Bold, color = Color(0xFF006E2A))
                        Text(receta.ingredientes, fontSize = 14.sp)
                    }
                    Column {
                        Text("Preparación", fontWeight = FontWeight.Bold, color = Color(0xFF006E2A))
                        Text(receta.pasos, fontSize = 14.sp)
                    }

                    if (nutricion != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🍎 Análisis Nutricional", fontWeight = FontWeight.Bold, color = Color(0xFF006E2A))
                                Text("• Calorías: ${nutricion!!.calorias}", fontSize = 12.sp)
                                Text("• Proteínas: ${nutricion!!.proteinas}", fontSize = 12.sp)
                                Text("• Grasas: ${nutricion!!.grasas}", fontSize = 12.sp)
                                Text("• Carbos: ${nutricion!!.carbos}", fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("💡 Consejo: ${nutricion!!.consejo}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else if (!editMode) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isAnalizando = true
                                    val res = api.analizarNutricion(receta)
                                    res.onSuccess { nutricion = it }
                                    isAnalizando = false
                                }
                            },
                            enabled = !isAnalizando,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006E2A))
                        ) {
                            if (isAnalizando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            else Text("💡 Ver Análisis Nutricional")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (editMode) {
                Button(
                    onClick = {
                        onUpdate(receta.id, receta.copy(titulo = tituloEdit, ingredientes = ingredientesEdit, pasos = pasosEdit))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006E2A))
                ) {
                    Text("Guardar Cambios")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar", color = Color(0xFF632CE5))
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// PROCESO DE NOTIFICACIONES (REQUISITO DE PRÁCTICA)
// ─────────────────────────────────────────────────────────────────────────────

private const val CHANNEL_ID = "albahaca_recetas"

/**
 * Crea el canal de notificaciones necesario para Android 8.0+
 */
private fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Canal de Recetas"
        val descriptionText = "Avisos de creación de recetas"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Lanza la notificación local al usuario
 */
private fun enviarNotificacionExito(context: Context, tituloReceta: String) {
    android.util.Log.d("DEPURACION_ALBAHACA", "Intentando enviar notificación para: $tituloReceta")
    
    // Verificación de permiso para notificaciones en Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.w("DEPURACION_ALBAHACA", "Sin permiso POST_NOTIFICATIONS, cancelando aviso.")
            return
        }
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_save)
        .setContentTitle("¡Receta Guardada!")
        .setContentText("Tu receta '$tituloReceta' ya está en Railway.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    try {
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
            android.util.Log.d("DEPURACION_ALBAHACA", "Notificación enviada con éxito")
        }
    } catch (e: SecurityException) {
        android.util.Log.e("DEPURACION_ALBAHACA", "Error de seguridad en notificación: ${e.message}")
    } catch (e: Exception) {
        android.util.Log.e("DEPURACION_ALBAHACA", "Error al enviar notificación: ${e.message}")
    }
}

@Composable
fun IngredientChip(name: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC3D8).copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.Close, null, tint = Color(0xFF7A7487), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun RecipeCard(title: String, time: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.Gray))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color(0xFF494455), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(time, fontSize = 12.sp, color = Color(0xFF494455))
                }
            }
        }
    }
}

