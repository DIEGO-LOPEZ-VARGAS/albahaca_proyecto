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
     */
    suspend fun loginUsuario(context: Context, usuario: String, contrasena: String): Boolean {
        return try {
            Log.d("DEPURACION_ALBAHACA", "Enviando POST de Login Real a: $BASE_URL/login")

            // Mandamos la petición POST a tu backend de Ktor
            val response: HttpResponse = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                // 💡 CORRECCIÓN TÉCNICA: Mapeamos la variable local 'contrasena' al atributo 'password' del KtorClient
                setBody(LoginRequest(usuario = usuario, password = contrasena))
            }

            // Registramos la petición en tu repositorio local de historial
            val repo = RailwayRepository()
            repo.guardarPeticion("POST", "/login", response.status.value)

            // Si el servidor backend responde que el usuario es válido (HTTP 200 OK)
            if (response.status == HttpStatusCode.OK) {

                // Guardamos el token recibido en el KtorClient para no romper tus otras pantallas (como Actividades)
                val loginData = response.body<LoginResponse>()
                KtorClient.sessionToken = loginData.token
                Log.d("DEPURACION_ALBAHACA", "TOKEN GUARDADO EN HISTORIAL: ${KtorClient.sessionToken}")

                // 🔥 DISPARAMOS LA NOTIFICACIÓN NATIVA DESDE AQUÍ
                mostrarNotificacionNativa(context)

                true
            } else {
                Log.w("DEPURACION_ALBAHACA", "Login fallido en servidor con código: ${response.status.value}")
                false
            }
        } catch (e: Exception) {
            Log.e("DEPURACION_ALBAHACA", "Falla de red o de host en loginUsuario: ${e.message}")
            e.printStackTrace()
            false
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
        notificationManager.notify(2002, builder.build())
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