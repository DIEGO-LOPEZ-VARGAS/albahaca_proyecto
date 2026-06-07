package com.example.albahacaproyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun RailwayStatusScreen() {
    val verdePrincipal      = Color(0xFF2E5A39)
    val verdeFondoIcono     = Color(0xFFE8F0EA)
    val grisTextoSecundario = Color(0xFF6B7280)
    val rojoAlertaFondo     = Color(0xFFFDE8E8)
    val rojoAlertaTexto     = Color(0xFF9B1C1C)

    var serverOnline  by remember { mutableStateOf(false) }
    var latencyMs     by remember { mutableStateOf<Long?>(null) }
    var routes        by remember { mutableStateOf<List<RouteInfo>>(emptyList()) }
    var historial     by remember { mutableStateOf<List<HistorialEntry>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(false) }
    var mensajeError  by remember { mutableStateOf<String?>(null) }

    val scope      = rememberCoroutineScope()
    val repository = remember { RailwayRepository() }

    LaunchedEffect(Unit) {
        historial = repository.obtenerHistorial()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    "Ruteo Railway",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Tarjeta estado del servidor
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = if (serverOnline) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (serverOnline) "Servidor en linea" else "Servidor fuera de linea",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "URL: http://10.0.2.2:8080",
                            fontSize = 12.sp,
                            color = grisTextoSecundario
                        )
                        latencyMs?.let {
                            Text("Latencia: ${it}ms", fontSize = 12.sp, color = grisTextoSecundario)
                        }
                        mensajeError?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, fontSize = 11.sp, color = rojoAlertaTexto)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    mensajeError = null
                                    try {
                                        val resultado = RailwayKtorService.obtenerEstado()
                                        serverOnline = resultado.online
                                        latencyMs    = resultado.latencyMs
                                        routes       = resultado.routes
                                        repository.guardarPeticion("GET", "/api/railway/status", 200)
                                    } catch (e: Exception) {
                                        serverOnline = false
                                        mensajeError = "Sin conexion: ${e.message}"
                                        repository.guardarPeticion("GET", "/api/railway/status", 503)
                                    }
                                    historial = repository.obtenerHistorial()
                                    isLoading = false
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Verificar servidor")
                            }
                        }
                    }
                }
            }

            // Rutas disponibles
            item {
                Text(
                    "Rutas disponibles (API)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (routes.isEmpty()) {
                item {
                    Text(
                        "Toca Verificar servidor para cargar las rutas.",
                        fontSize = 13.sp,
                        color = grisTextoSecundario,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                items(routes) { ruta ->
                    FilaRuta(ruta)
                }
            }

            // Historial local
            item {
                Text(
                    "Historial local",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (historial.isEmpty()) {
                item {
                    Text(
                        "Sin registros aun.",
                        fontSize = 13.sp,
                        color = grisTextoSecundario,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                items(historial) { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (entry.statusCode in 200..299) verdeFondoIcono else rojoAlertaFondo,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(entry.timestamp, fontSize = 11.sp, color = grisTextoSecundario)
                        Text(entry.method, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = verdePrincipal)
                        Text(entry.ruta, fontSize = 12.sp, color = Color(0xFF1F2937))
                        Text(
                            entry.statusCode.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.statusCode in 200..299) Color(0xFF4CAF50) else rojoAlertaTexto
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun FilaRuta(ruta: RouteInfo) {
    val (color, fondo) = when (ruta.method) {
        "GET"    -> Pair(Color(0xFF1565C0), Color(0xFFE3F2FD))
        "POST"   -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
        "PUT"    -> Pair(Color(0xFFE65100), Color(0xFFFFF3E0))
        "DELETE" -> Pair(Color(0xFFC62828), Color(0xFFFFEBEE))
        else     -> Pair(Color.Gray,        Color(0xFFF5F5F5))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ruta.method,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier
                .background(fondo, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(ruta.path, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1F2937))
            Text(ruta.description, fontSize = 11.sp, color = Color(0xFF6B7280))
        }
    }
}