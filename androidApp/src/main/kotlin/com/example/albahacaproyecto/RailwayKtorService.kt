package com.example.albahacaproyecto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log // AGREGADO para mantener las etiquetas de depuración
import androidx.core.app.NotificationCompat
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.* // AGREGADO para manejar HttpResponse
import io.ktor.http.* // AGREGADO para los estados de HTTP (HttpStatusCode)

// ─────────────────────────────────────────────────────────────────────────────
// MODELOS LOCALES DE UI
// ─────────────────────────────────────────────────────────────────────────────

/** Registro guardado localmente de cada petición realizada */
data class HistorialEntry(
    val id: Int,
    val timestamp: String,
    val method: String,
    val ruta: String,
    val statusCode: Int
)

// 💡 NOTA: Se eliminó la declaración de LoginRequest local.
// Automáticamente se mapeará con la data class @Serializable de tu KtorClient.kt

// ─────────────────────────────────────────────────────────────────────────────
// SERVICIO HTTP
// ─────────────────────────────────────────────────────────────────────────────

object RailwayKtorService {

    private val client = KtorClient.client
    private val BASE_URL = KtorClient.BASE_URL

    /**
     * POST /login - Envía los datos al backend y lanza la notificación si es exitoso
     * Retorna NULL si es exitoso, o el mensaje de error si falla.
     */
    suspend fun loginUsuario(context: Context, usuario: String, contrasena: String): String? {
        return try {
            Log.d("DEPURACION_ALBAHACA", "Enviando POST de Login Real a: $BASE_URL/login")

            val response: HttpResponse = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(usuario = usuario, password = contrasena))
            }

            val repo = RailwayRepository()
            repo.guardarPeticion("POST", "/login", response.status.value)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val loginData = response.body<LoginResponse>()
                    KtorClient.sessionToken = loginData.token
                    KtorClient.userName = loginData.nombre
                    Log.d("DEPURACION_ALBAHACA", "LOGIN EXITOSO: Token obtenido.")
                    mostrarNotificacionNativa(context)
                    null // Éxito
                }
                HttpStatusCode.Unauthorized -> "Usuario o contraseña incorrectos"
                HttpStatusCode.NotFound -> "Servidor no responde (404)"
                HttpStatusCode.BadGateway -> "El servidor se está despertando, intenta de nuevo en 5 segundos"
                else -> "Error del servidor: ${response.status.value}"
            }
        } catch (e: Exception) {
            Log.e("DEPURACION_ALBAHACA", "Falla de red en loginUsuario: ${e.message}")
            "Error de red: El servidor no está disponible en este momento"
        }
    }

    /**
     * Función que genera la alerta nativa en la barra de estado de Android
     */
    private fun mostrarNotificacionNativa(context: Context) {
        val channelId = "canal_albahaca_alertas"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal obligatorio para Android 8.0 o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Notificaciones del Sistema",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Construcción de la notificación
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono nativo del sistema
                .setContentTitle("Albahaca App")
                .setContentText("¡Inicio de sesión correcto!.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

        // Mostrarla en el celular
        try {
            notificationManager.notify(2002, builder.build())
            Log.d("DEPURACION_ALBAHACA", "Notificación de login mostrada")
        } catch (e: SecurityException) {
            Log.e("DEPURACION_ALBAHACA", "Sin permiso para mostrar notificación de login: ${e.message}")
        } catch (e: Exception) {
            Log.e("DEPURACION_ALBAHACA", "Error al mostrar notificación de login: ${e.message}")
        }
    }

    /**
     * GET /api/railway/status
     */
    suspend fun obtenerEstado(): RailwayStatusResponse {
        return client.get("$BASE_URL/api/railway/status").body()
    }

    /**
     * GET /api/railway/ping
     */
    suspend fun ping(): Long {
        val inicio = System.currentTimeMillis()
        client.get("$BASE_URL/api/railway/ping")
        return System.currentTimeMillis() - inicio
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL
// ─────────────────────────────────────────────────────────────────────────────

class RailwayRepository {

    companion object {
        private val _historial = mutableListOf<HistorialEntry>()
        private var _nextId = 1
    }

    suspend fun guardarPeticion(method: String, ruta: String, statusCode: Int) {
        val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        _historial.add(
            0,
            HistorialEntry(
                id         = _nextId++,
                timestamp  = hora,
                method     = method,
                ruta       = ruta,
                statusCode = statusCode
            )
        )
    }

    suspend fun obtenerHistorial(): List<HistorialEntry> = _historial.take(15)
}