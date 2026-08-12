package com.example.albahacaproyecto

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity // 🔥 IMPORTACIÓN AGREGADA PARA LA HUELLA
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.albahacaproyecto.database.OfflineRepository
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() { // 🔥 CAMBIADO DE ComponentActivity A FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            val scope = rememberCoroutineScope()
            
            var isLoggedIn by remember { mutableStateOf(false) }
            var mostrarAlertaCaducidad by remember { mutableStateOf(false) }
            var itemsPorCaducar by remember { mutableStateOf<List<String>>(emptyList()) }

            // Función reutilizable para checar caducidad
            fun checkCaducidad() {
                scope.launch {
                    try {
                        val offlineRepo = OfflineRepository(context)
                        val lista = offlineRepo.getFrutas()
                        val hoy = java.util.Calendar.getInstance().timeInMillis
                        val urgentes = lista.filter { f ->
                            if (f.fechaCaducidad.isNotBlank()) {
                                val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                val fecha = format.parse(f.fechaCaducidad)
                                fecha != null && (fecha.time - hoy) < (2 * 24 * 60 * 60 * 1000) // 2 días
                            } else false
                        }.map { it.nombre }
                        
                        if (urgentes.isNotEmpty()) {
                            itemsPorCaducar = urgentes
                            mostrarAlertaCaducidad = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ALERTA", "Falla al revisar caducidad")
                    }
                }
            }

            // Cargar sesión persistente al iniciar
            LaunchedEffect(Unit) {
                // Configurar el manejador de sesión expirada
                KtorClient.onSessionExpired = {
                    sessionManager.clearSession()
                    isLoggedIn = false
                }

                val savedToken = sessionManager.getToken()
                val savedName = sessionManager.getUserName()
                if (savedToken != null) {
                    KtorClient.sessionToken = savedToken
                    // 🔥 SE ELIMINÓ: isLoggedIn = true
                    // Ahora la App carga el token pero se detiene en el Login para pedir la huella
                }
                if (savedName != null) {
                    KtorClient.userName = savedName
                }
            }

            // Ejecutar chequeo cada vez que se loguea (manual o auto)
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    checkCaducidad()
                }
            }

            if (mostrarAlertaCaducidad) {
                AlertDialog(
                    onDismissRequest = { mostrarAlertaCaducidad = false },
                    title = { Text("⚠️ ¡Alerta de Caducidad!") },
                    text = {
                        Text("Los siguientes productos caducan pronto o ya expiraron: \n\n• ${itemsPorCaducar.joinToString("\n• ")}")
                    },
                    confirmButton = {
                        Button(onClick = { mostrarAlertaCaducidad = false }) { Text("Entendido") }
                    }
                )
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        DefinitiveLoginScreen(onLoginExitoso = { isLoggedIn = true })
                    } else {
                        MainMenuScreen(onCerrarSesion = {
                            sessionManager.clearSession()
                            isLoggedIn = false
                        })
                    }
                }
            }
        }
    }
}