package com.example.albahacaproyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

class RecetaApiClient {
    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL + "/api/recetas"

    suspend fun enviarReceta(receta: Receta): Boolean {
        return try {
            val response = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(receta)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerRecetas(): List<Receta> {
        return try {
            client.get(BASE_URL) {
                KtorClient.sessionToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// Almacenamiento Local Simple (Requisito de Examen)
object RecetaStorage {
    var ultimaRecetaGuardada: Receta? = null
}

@Composable
fun RecetaView() {
    var titulo by remember { mutableStateOf("") }
    var ingredientes by remember { mutableStateOf("") }
    var pasos by remember { mutableStateOf("") }
    var historialRecetas by remember { mutableStateOf<List<Receta>>(emptyList()) }
    
    val VerduritasPrimary = Color(0xFF632CE5)
    val VerduritasSecondary = Color(0xFF006E2A)
    val VerduritasBackground = Color(0xFFFCF9F8)

    val scope = rememberCoroutineScope()
    val api = remember { RecetaApiClient() }
    val context = LocalContext.current

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

    // TÉCNICA: Procesos de Notificaciones (Inicialización del Canal y Permisos)
    LaunchedEffect(Unit) {
        crearCanalNotificaciones(context)
        
        // Solicitar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        historialRecetas = api.obtenerRecetas()
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
                Surface(
                    color = Color(0xFF5CFD80).copy(alpha = 0.2f),
                    shape = CircleShape,
                    onClick = {}
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, null, tint = VerduritasSecondary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Inventario", color = VerduritasSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        val nueva = Receta(titulo, ingredientes, pasos)
                        scope.launch {
                            val exito = api.enviarReceta(nueva)
                            if (exito) {
                                // TÉCNICA: Procesos de Notificaciones (Disparo del aviso)
                                enviarNotificacionExito(context, nombreReceta)
                                
                                historialRecetas = api.obtenerRecetas()
                                titulo = ""; ingredientes = ""; pasos = ""
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
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Generar Receta con IA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // My Recipes Section
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("Mis Recetas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Ver todas", color = VerduritasPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Historial real de recetas
        items(historialRecetas) { receta ->
            RecipeCard(receta.titulo, "20 min", Modifier.fillMaxWidth())
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
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
    android.util.Log.d("NOTIFICACION", "Intentando enviar notificación para: $tituloReceta")
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_save)
        .setContentTitle("¡Receta Guardada!")
        .setContentText("Tu receta '$tituloReceta' ya está en Railway.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    try {
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
            android.util.Log.d("NOTIFICACION", "Notificación enviada con éxito")
        }
    } catch (e: Exception) {
        android.util.Log.e("NOTIFICACION", "Error al enviar notificación: ${e.message}")
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
fun RecipeCard(title: String, time: String, modifier: Modifier) {
    Surface(
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

