package com.example.albahacaproyecto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@Composable
fun FrutaFormView() {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var mensajeEstatus by remember { mutableStateOf("Esperando registro...") }

    // Estado de Compose que forzará a la pantalla a redibujarse cuando cambie la lista
    var historialDeFrutas by remember { mutableStateOf("No hay frutas registradas en el servidor.") }

    val coroutineScope = rememberCoroutineScope()
    val apiCliente = remember { FrutaApiClient() }

    // Al abrir la pantalla por primera vez, mandamos a traer lo que tenga el servidor
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val resultadoInicial = apiCliente.obtenerFrutas()
                if (resultadoInicial.isNotBlank()) {
                    historialDeFrutas = resultadoInicial
                }
            } catch (e: Exception) {
                historialDeFrutas = "Error al cargar historial inicial."
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro de Frutas", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la fruta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val cantInt = cantidad.toIntOrNull() ?: 0
                val frutaNueva = Fruta(nombre, cantInt)
                LocalStorage.ultimaFruta = frutaNueva

                coroutineScope.launch {
                    mensajeEstatus = "Enviando al backend..."

                    // 1. Enviamos el registro al backend
                    val respuesta = apiCliente.enviarFruta(frutaNueva)
                    mensajeEstatus = "Local: ¡Guardado! \nBackend: $respuesta"

                    // 2. Traemos la lista actualizada inmediatamente
                    val listaActualizada = apiCliente.obtenerFrutas()

                    // 3. Forzamos la actualización de la UI en el hilo principal
                    if (listaActualizada.isNotBlank()) {
                        historialDeFrutas = listaActualizada
                    }

                    // Limpiamos los campos de texto para el siguiente registro
                    nombre = ""
                    cantidad = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRAR")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = mensajeEstatus)

        // SECCIÓN VISUAL DEL HISTORIAL
        Spacer(modifier = Modifier.height(24.dp))
        Text("--- FRUTAS EN EL SERVIDOR ---", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Este Text leerá el estado mutable y cambiará el texto en tu pantalla al instante
        Text(text = historialDeFrutas, style = MaterialTheme.typography.bodyMedium)
    }
}