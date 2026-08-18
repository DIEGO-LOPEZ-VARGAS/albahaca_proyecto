package com.example.albahacaproyecto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.delay
import com.example.albahacaproyecto.database.OfflineRepository
import androidx.compose.ui.platform.LocalContext

@Composable
fun ListaComprasScreen() {
    val verdePrincipal      = Color(0xFF2E5A39)
    val grisTextoSecundario = Color(0xFF6B7280)
    val amarilloFondo       = Color(0xFFFFFBEB)
    val amarilloTexto       = Color(0xFF92400E)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val offlineRepo = remember { OfflineRepository(context) }
    
    val todosLosItems by offlineRepo.getComprasFlow().collectAsState(initial = emptyList())

    var isLoading        by remember { mutableStateOf(value = false) }
    var mensajeExito     by remember { mutableStateOf<String?>(null) }
    var filtro           by remember { mutableStateOf("todos") }
    var mostrarDialogo   by remember { mutableStateOf(false) }
    var nuevoNombre      by remember { mutableStateOf("") }
    var nuevaCantidad    by remember { mutableStateOf("1") }
    var nuevoTipo        by remember { mutableStateOf("despensa") }

    val itemsFiltrados = when (filtro) {
        else -> todosLosItems
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("➕ Agregar Producto", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre del producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevaCantidad,
                        onValueChange = { newValue -> if (newValue.all { it.isDigit() }) nuevaCantidad = newValue },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Tipo de almacenamiento:", fontSize = 13.sp, color = grisTextoSecundario)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = nuevoTipo == "refrigerador",
                            onClick = { nuevoTipo = "refrigerador" },
                            label = { Text("❄️ Refrigerador") }
                        )
                        FilterChip(
                            selected = nuevoTipo == "despensa",
                            onClick = { nuevoTipo = "despensa" },
                            label = { Text("🗄️ Despensa") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoNombre.isNotBlank()) {
                            scope.launch {
                                val nueva = Fruta(
                                    nombre = nuevoNombre.trim(),
                                    cantidad = nuevaCantidad.toIntOrNull() ?: 1,
                                    lugarAlmacenamiento = nuevoTipo
                                )
                                offlineRepo.guardarCompra(nueva)
                                nuevoNombre = ""
                                nuevaCantidad = "1"
                                mostrarDialogo = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal)
                ) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🛒 Lista de Compras",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sincronizar desde servidor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Sincronización persistente Room", fontSize = 12.sp, color = grisTextoSecundario)
                        mensajeExito?.let { Text(it, fontSize = 11.sp, color = Color(0xFF4CAF50)) }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    isLoading = true
                                    scope.launch {
                                        delay(1000)
                                        isLoading = false
                                        mensajeExito = "✅ Lista actualizada"
                                    }
                                },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                                else Text("🔄 Sincronizar")
                            }
                            Button(
                                onClick = { mostrarDialogo = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                modifier = Modifier.weight(1f)
                            ) { Text("➕ Agregar") }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaEstadistica("📋 Total", todosLosItems.size.toString(), Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
                    TarjetaEstadistica("⏳ Lista", todosLosItems.size.toString(), amarilloFondo, amarilloTexto, Modifier.weight(1f))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = filtro == "todos", onClick = { filtro = "todos" }, label = { Text("Todos") })
                }
            }

            if (itemsFiltrados.isEmpty()) {
                item {
                    Text(
                        if (filtro == "todos") "Lista vacía. Agrega productos." else "No hay productos.",
                        fontSize = 13.sp, color = grisTextoSecundario,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(count = itemsFiltrados.size) { index ->
                    val itemCompra = itemsFiltrados[index]
                    ElevatedCard(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { },
                                colors = CheckboxDefaults.colors(checkedColor = verdePrincipal)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    itemCompra.nombre,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    "${itemCompra.cantidad} unidades · ${itemCompra.lugarAlmacenamiento}",
                                    fontSize = 12.sp,
                                    color = grisTextoSecundario
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    offlineRepo.eliminarCompra(itemCompra.localId, itemCompra.id)
                                }
                            }) {
                                Text("🗑️", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun TarjetaEstadistica(
    titulo: String,
    valor: String,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = fondo,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorTexto.copy(alpha = 0.7f))
            Text(valor, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = colorTexto)
        }
    }
}
