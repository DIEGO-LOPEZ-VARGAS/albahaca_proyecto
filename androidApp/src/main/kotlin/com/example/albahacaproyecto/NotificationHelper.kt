package com.example.albahacaproyecto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object NotificationHelper {
    private const val CHANNEL_ID_ALERTS = "albahaca_alerts"
    private const val CHANNEL_ID_RECIPES = "albahaca_recetas"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal para Alertas de Caducidad
            val channelAlerts = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Alertas de Alimentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre productos por caducar"
            }
            
            // Canal para Recetas Guardadas
            val channelRecipes = NotificationChannel(
                CHANNEL_ID_RECIPES,
                "Recetas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones al guardar recetas"
            }

            notificationManager.createNotificationChannel(channelAlerts)
            notificationManager.createNotificationChannel(channelRecipes)
        }
    }

    fun enviarAlertaCaducidad(context: Context, items: List<String>) {
        if (!hasNotificationPermission(context)) return

        val message = "Los siguientes productos vencen pronto: \n• ${items.joinToString("\n• ")}"
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ ¡Alimentos por Caducar!")
            .setContentText("Tienes ${items.size} productos en riesgo")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(1001, builder.build())
            } catch (e: SecurityException) {
                // Manejar error si el permiso se revocó
            }
        }
    }

    fun enviarNotificacionReceta(context: Context, titulo: String) {
        if (!hasNotificationPermission(context)) return

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_RECIPES)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("¡Receta Guardada!")
            .setContentText("Tu receta '$titulo' ya está segura.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) { }
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}
