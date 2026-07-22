package com.example.albahacaproyecto

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity // 🔥 IMPORTACIÓN AGREGADA PARA LA HUELLA
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : FragmentActivity() { // 🔥 CAMBIADO DE ComponentActivity A FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        DefinitiveLoginScreen(onLoginExitoso = { isLoggedIn = true })
                    } else {
                        MainMenuScreen()
                    }
                }
            }
        }
    }
}