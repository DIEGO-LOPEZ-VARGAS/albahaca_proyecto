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
fun ProductosScreen() {
    val verdePrincipal      = Color(0xFF2E5A39)
    val verdeFondoIcono     = Color(0xFFE8F0EA)
    val grisTextoSecundario = Color(0xFF6B7280)
    val rojoAlertaFondo     = Color(0xFFFDE8E8)
    val rojoAlertaTexto     = Color(0xFF9B1C1C)
    val azulFondo           = Color(0xFFE3F2FD)
    val azulTexto           = Color(0xFF1565C0)

    var productos       by remember { mutableStateOf<List<ProductoLocal>>(emptyList()) }
    var ramaInfo        by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }
    var mensajeError    by remember { mutableStateOf<String?>(null) }
    var mensajeExito    by remember { mutableStateOf<String?>(null) }

    val scope      = rememberCoroutineScope()
    val repository = remember { ProductosRepository() }

    LaunchedEffect(Unit) {
        productos = repository.obtenerProductosLocales()
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
                    "Productos Rama 2",
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

            // ── Tarjeta principal ──────────────────────────────────────────
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Sincronización Rama 2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Endpoint: GET /api/rama2/productos",
                            fontSize = 12.sp,
                            color = grisTextoSecundario
                        )
                        if (ramaInfo.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                ramaInfo,
                                fontSize = 12.sp,
                                color = verdePrincipal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        mensajeError?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, fontSize = 11.sp, color = rojoAlertaTexto)
                        }
                        mensajeExito?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, fontSize = 11.sp, color = Color(0xFF4CAF50))
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    mensajeError = null
                                    mensajeExito = null
                                    try {
                                        val respuesta = ProductosService.obtenerProductos()
                                        repository.guardarProductos(respuesta.productos)
                                        productos = repository.obtenerProductosLocales()
                                        ramaInfo = "${respuesta.rama} · ${respuesta.total} productos"
                                        mensajeExito = "✅ Datos guardados localmente"
                                    } catch (e: Exception) {
                                        mensajeError = "Sin conexión: ${e.message}"
                                    }
                                    isLoading = false
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Sincronizar y Registrar Datos")
                            }
                        }
                    }
                }
            }

            // ── Lista de productos ─────────────────────────────────────────
            item {
                Text(
                    "📦 Almacenamiento Local Registrado",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (productos.isEmpty()) {
                item {
                    Text(
                        "Toca Sincronizar para cargar los productos.",
                        fontSize = 13.sp,
                        color = grisTextoSecundario,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                items(productos) { producto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (producto.tipo_almacenamiento == "refrigerador")
                                    azulFondo else verdeFondoIcono,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = if (producto.disponible)
                                            Color(0xFF4CAF50) else Color(0xFFF44336),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    producto.nombre_producto,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    "Rol: Rama 2",
                                    fontSize = 12.sp,
                                    color = grisTextoSecundario
                                )
                                Text(
                                    "Caduca: ${producto.fecha_caducidad}",
                                    fontSize = 11.sp,
                                    color = rojoAlertaTexto
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                if (producto.disponible) "Activo" else "Offline",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (producto.disponible)
                                    Color(0xFF4CAF50) else rojoAlertaTexto
                            )
                            Text(
                                producto.tipo_almacenamiento,
                                fontSize = 11.sp,
                                color = if (producto.tipo_almacenamiento == "refrigerador")
                                    azulTexto else verdePrincipal
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}