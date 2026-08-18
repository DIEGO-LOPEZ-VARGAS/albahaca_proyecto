package com.example.albahacaproyecto

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
<<<<<<< Updated upstream
            var isLoggedIn by remember { mutableStateOf(false) }
=======
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            val scope = rememberCoroutineScope()

            var isLoggedIn by remember { mutableStateOf(false) }
            var esAdmin by remember { mutableStateOf(false) }

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
                KtorClient.onSessionExpired = {
                    sessionManager.clearSession()
                    isLoggedIn = false
                    esAdmin = false
                }

                val savedToken = sessionManager.getToken()
                val savedName = sessionManager.getUserName()
                if (savedToken != null) {
                    KtorClient.sessionToken = savedToken
                }
                if (savedName != null) {
                    KtorClient.userName = savedName
                }
            }

            // Ejecutar chequeo cada vez que se loguea (solo para usuarios normales)
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn && !esAdmin) {
                    checkCaducidad()
                }
            }

            if (mostrarAlertaCaducidad && !esAdmin) {
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
>>>>>>> Stashed changes

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
<<<<<<< Updated upstream
                        DefinitiveLoginScreen(onLoginExitoso = { isLoggedIn = true })
                    } else {
                        MainMenuScreen()
=======
                        DefinitiveLoginScreen(onLoginExitoso = { usuarioEsAdmin ->
                            esAdmin = usuarioEsAdmin
                            isLoggedIn = true
                        })
                    } else {
                        if (esAdmin) {
                            AdminMenuScreen(onCerrarSesion = {
                                sessionManager.clearSession()
                                isLoggedIn = false
                                esAdmin = false
                            })
                        } else {
                            MainMenuScreen(onCerrarSesion = {
                                sessionManager.clearSession()
                                isLoggedIn = false
                                esAdmin = false
                            })
                        }
>>>>>>> Stashed changes
                    }
                }
            }
        }
    }
}