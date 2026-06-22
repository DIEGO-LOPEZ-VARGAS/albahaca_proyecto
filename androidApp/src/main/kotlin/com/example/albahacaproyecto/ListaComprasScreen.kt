package com.example.albahacaproyecto

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// MODELO LOCAL
// ─────────────────────────────────────────────────────────────────────────────

data class ItemCompra(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val tipo: String,
    var comprado: Boolean = false
)

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORIO LOCAL DE COMPRAS
// ─────────────────────────────────────────────────────────────────────────────

object ComprasRepository {
    private val _items = mutableStateListOf<ItemCompra>()
    val items: List<ItemCompra> get() = _items

    fun agregarDesdeBackend(productos: List<ProductoLocal>) {
        productos.forEach { p ->
            if (_items.none { it.id == p.id }) {
                _items.add(ItemCompra(p.id, p.nombre_producto, p.cantidad.coerceAtLeast(1), p.tipo_almacenamiento))
            }
        }
    }

    fun marcarComprado(id: Int) {
        val idx = _items.indexOfFirst { it.id == id }
        if (idx >= 0) _items[idx] = _items[idx].copy(comprado = !_items[idx].comprado)
    }

    fun eliminar(id: Int) {
        _items.removeAll { it.id == id }
    }

    fun limpiarComprados() {
        _items.removeAll { it.comprado }
    }

    fun limpiarLista() {
        _items.clear()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaComprasScreen() {
    val verdePrincipal      = Color(0xFF2E5A39)
    val grisTextoSecundario = Color(0xFF6B7280)
    val rojoAlertaTexto     = Color(0xFF9B1C1C)
    val amarilloFondo       = Color(0xFFFFFBEB)
    val amarilloTexto       = Color(0xFF92400E)
    val verdeComprado       = Color(0xFFE8F5E9)

    var isLoading        by remember { mutableStateOf(false) }
    var mensajeError     by remember { mutableStateOf<String?>(null) }
    var mensajeExito     by remember { mutableStateOf<String?>(null) }
    var filtro           by remember { mutableStateOf("todos") } // todos, pendientes, comprados
    var mostrarDialogo   by remember { mutableStateOf(false) }
    var nuevoNombre      by remember { mutableStateOf("") }
    var nuevaCantidad    by remember { mutableStateOf("1") }
    var nuevoTipo        by remember { mutableStateOf("despensa") }

    val scope      = rememberCoroutineScope()
    val repository = remember { ProductosRepository() }
    val items      = ComprasRepository.items

    val itemsFiltrados = when (filtro) {
        "pendientes" -> items.filter { !it.comprado }
        "comprados"  -> items.filter { it.comprado }
        else         -> items
    }

    // ── Diálogo agregar producto ──────────────────────────────────────────
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
                        onValueChange = { nuevaCantidad = it.filter { c -> c.isDigit() } },
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
                                try {
                                    val nuevaCompra = Producto(
                                        id = 0,
                                        nombre_producto = nuevoNombre.trim(),
                                        cantidad = nuevaCantidad.toIntOrNull() ?: 1,
                                        fecha_caducidad = "N/A",
                                        tipo_almacenamiento = nuevoTipo,
                                        disponible = true
                                    )
                                    ProductosService.agregarCompra(nuevaCompra)
                                    
                                    // Sincronizar después de agregar
                                    val respuesta = ProductosService.obtenerCompras()
                                    repository.guardarProductos(respuesta.productos)
                                    // Recargar lista local
                                    val locales = repository.obtenerProductosLocales()
                                    ComprasRepository.limpiarLista() // Nuevo método para evitar duplicados
                                    ComprasRepository.agregarDesdeBackend(locales)
                                    
                                    nuevoNombre = ""
                                    nuevaCantidad = "1"
                                    mostrarDialogo = false
                                } catch (e: Exception) {
                                    mensajeError = "Error al guardar: ${e.message}"
                                }
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
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    "🛒 Lista de Compras",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Tarjeta sincronizar ────────────────────────────────────────
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sincronizar desde servidor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("GET /api/rama2/compras", fontSize = 12.sp, color = grisTextoSecundario)
                        mensajeError?.let { Text(it, fontSize = 11.sp, color = rojoAlertaTexto) }
                        mensajeExito?.let { Text(it, fontSize = 11.sp, color = Color(0xFF4CAF50)) }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        mensajeError = null
                                        mensajeExito = null
                                        try {
                                            val respuesta = ProductosService.obtenerCompras()
                                            repository.guardarProductos(respuesta.productos)
                                            val locales = repository.obtenerProductosLocales()
                                            ComprasRepository.agregarDesdeBackend(locales)
                                            mensajeExito = "✅ ${respuesta.total} productos cargados"
                                        } catch (e: Exception) {
                                            mensajeError = "Sin conexión: ${e.message}"
                                        }
                                        isLoading = false
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

            // ── Estadísticas ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaEstadistica("📋 Total", "${items.size}", Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
                    TarjetaEstadistica("⏳ Pendientes", "${items.count { !it.comprado }}", amarilloFondo, amarilloTexto, Modifier.weight(1f))
                    TarjetaEstadistica("✅ Comprados", "${items.count { it.comprado }}", verdeComprado, verdePrincipal, Modifier.weight(1f))
                }
            }

            // ── Filtros ───────────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = filtro == "todos", onClick = { filtro = "todos" }, label = { Text("Todos") })
                    FilterChip(selected = filtro == "pendientes", onClick = { filtro = "pendientes" }, label = { Text("Pendientes") })
                    FilterChip(selected = filtro == "comprados", onClick = { filtro = "comprados" }, label = { Text("Comprados") })
                }
            }

            // ── Botón limpiar comprados ───────────────────────────────────
            if (items.any { it.comprado }) {
                item {
                    TextButton(
                        onClick = { ComprasRepository.limpiarComprados() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = rojoAlertaTexto,
                            disabledContentColor = Color.Gray
                        )
                    ) { Text("🗑️ Eliminar comprados") }
                }
            }

            // ── Lista ─────────────────────────────────────────────────────
            if (itemsFiltrados.isEmpty()) {
                item {
                    Text(
                        if (filtro == "todos") "Lista vacía. Sincroniza o agrega productos." else "No hay productos en esta categoría.",
                        fontSize = 13.sp, color = grisTextoSecundario,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(itemsFiltrados, key = { it.id }) { item ->
                    ElevatedCard(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (item.comprado) verdeComprado else Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.comprado,
                                onCheckedChange = { nuevoEstado ->
                                    scope.launch {
                                        try {
                                            ProductosService.actualizarCompra(item.id, nuevoEstado)
                                            ComprasRepository.marcarComprado(item.id)
                                        } catch (e: Exception) {
                                            mensajeError = "Error al actualizar"
                                        }
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = verdePrincipal)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.nombre,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = if (item.comprado) grisTextoSecundario else Color(0xFF111827),
                                    style = if (item.comprado) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                                )
                                Text(
                                    "${item.cantidad} unidades • ${item.tipo}",
                                    fontSize = 12.sp,
                                    color = grisTextoSecundario
                                )
                            }
                            IconButton(onClick = { 
                                scope.launch {
                                    try {
                                        ProductosService.eliminarCompra(item.id)
                                        ComprasRepository.eliminar(item.id)
                                    } catch (e: Exception) {
                                        mensajeError = "Error al eliminar"
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES AUXILIARES
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TarjetaEstadistica(titulo: String, valor: String, fondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontSize = 11.sp, color = colorTexto, fontWeight = FontWeight.Bold)
            Text(valor, fontSize = 20.sp, color = colorTexto, fontWeight = FontWeight.ExtraBold)
        }
    }
}
