package com.example.albahacaproyecto

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DefinitiveLoginScreen() {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        // ... (tus campos de texto aquí) ...

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    val codigo = KtorClient.enviarLogin(usuario, contrasena)

                    when (codigo) {
                        200 -> Toast.makeText(context, "Acceso concedido", Toast.LENGTH_SHORT).show()
                        401 -> Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Error de conexión: $codigo", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading
        ) {
            Text("Iniciar Sesión")
        }
    }
}