package com.example.albahacaproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controla qué pantalla se muestra
                    var pantallaActual by remember { mutableStateOf("login") }

                    when (pantallaActual) {
                        "login" -> DefinitiveLoginScreen(
                            onLoginExitoso = { pantallaActual = "menu" }
                        )
                        "menu"  -> MainMenuScreen()
                    }
                }
            }
        }
    }
}
