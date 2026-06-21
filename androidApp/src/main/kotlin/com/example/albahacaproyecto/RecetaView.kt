package com.example.albahacaproyecto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Receta(
    val titulo: String,
    val ingredientes: String,
    val pasos: String
)

class RecetaApiClient {
    private val client = HttpClient()
    private val BASE_URL = "https://backend-production-523ba.up.railway.app/api/recetas"

    suspend fun enviarReceta(receta: Receta): String {
        return try {
            // JSON manual para evitar configuraciones complejas en el examen
            val json = "{\"titulo\":\"${receta.titulo}\",\"ingredientes\":\"${receta.ingredientes}\",\"pasos\":\"${receta.pasos}\"}"
            val response = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            response.bodyAsText()
        } catch (e: Exception) {
            "Error: No se pudo conectar al servidor."
        }
    }

    suspend fun obtenerRecetas(): String {
        return try {
            val response = client.get(BASE_URL)
            response.bodyAsText()
        } catch (e: Exception) {
            "Servidor no disponible."
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
    var statusMessage by remember { mutableStateOf("Listo para crear") }
    var historialRecetas by remember { mutableStateOf("Cargando recetas...") }

    val scope = rememberCoroutineScope()
    val api = remember { RecetaApiClient() }

    LaunchedEffect(Unit) {
        historialRecetas = api.obtenerRecetas()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Gestor de Recetas", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la Receta") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = ingredientes,
                onValueChange = { ingredientes = it },
                label = { Text("Ingredientes (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = pasos,
                onValueChange = { pasos = it },
                label = { Text("Pasos de preparación") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            Button(
                onClick = {
                    val nuevaReceta = Receta(titulo, ingredientes, pasos)
                    // Guardar en local (Requisito examen)
                    RecetaStorage.ultimaRecetaGuardada = nuevaReceta
                    
                    scope.launch {
                        statusMessage = "Enviando al servidor..."
                        val res = api.enviarReceta(nuevaReceta)
                        statusMessage = res
                        historialRecetas = api.obtenerRecetas()
                        
                        // Limpiar campos
                        titulo = ""; ingredientes = ""; pasos = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GUARDAR RECETA")
            }
        }

        item {
            Text(statusMessage, style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text("--- RECETAS EN EL SERVIDOR ---", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(historialRecetas, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
