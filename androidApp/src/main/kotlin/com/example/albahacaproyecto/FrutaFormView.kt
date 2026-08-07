package com.example.albahacaproyecto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.albahacaproyecto.database.OfflineRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun FrutaFormView() {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var fechaCaducidad by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("Refri") }
    var ripeness by remember { mutableStateOf(10f) }
    
    val VerduritasPrimary = Color(0xFF632CE5)
    val VerduritasSecondary = Color(0xFF006E2A)
    val VerduritasBackground = Color(0xFFFCF9F8)
    val VerduritasSurface = Color(0xFFFFFFFF)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val offlineRepo = remember { OfflineRepository(context) }
    
    val historialDeFrutas by offlineRepo.getFrutasFlow().collectAsState(initial = emptyList())
    
    var queryFruta by remember { mutableStateOf("") }
    var frutaAEditar by remember { mutableStateOf<Fruta?>(null) }
    
    var mostrarCamara by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) mostrarCamara = true
        else android.widget.Toast.makeText(context, "Permiso de cámara denegado", android.widget.Toast.LENGTH_SHORT).show()
    }

    if (mostrarCamara) {
        AlertDialog(
            onDismissRequest = { mostrarCamara = false },
            confirmButton = {},
            title = { Text("Escanear Despensa") },
            text = {
                Box(modifier = Modifier.size(300.dp)) {
                    CameraPreview(onImageCaptured = { uri ->
                        mostrarCamara = false
                        coroutineScope.launch {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                            
                            if (originalBitmap != null) {
                                // Compresión: Reducimos el tamaño a máximo 1024px y 70% calidad
                                val scaledBitmap = if (originalBitmap.width > 1024) {
                                    val ratio = 1024f / originalBitmap.width
                                    Bitmap.createScaledBitmap(originalBitmap, 1024, (originalBitmap.height * ratio).toInt(), true)
                                } else originalBitmap
                                
                                val outputStream = ByteArrayOutputStream()
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                                val compressedBytes = outputStream.toByteArray()

                                android.widget.Toast.makeText(context, "Analizando imagen...", android.widget.Toast.LENGTH_SHORT).show()
                                val apiFruta = FrutaApiClient()
                                val res = apiFruta.analizarImagen(compressedBytes)
                                res.onSuccess { lista ->
                                    if (lista.isNotEmpty()) {
                                        nombre = lista[0].nombre
                                        cantidad = lista[0].cantidad.toString()
                                        android.widget.Toast.makeText(context, "¡Detectado!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    })
                }
            }
        )
    }

    if (frutaAEditar != null) {
        EditarFrutaDialog(
            fruta = frutaAEditar!!,
            onDismiss = { frutaAEditar = null },
            onUpdate = { _, nuevaFruta ->
                coroutineScope.launch {
                    offlineRepo.actualizarFruta(nuevaFruta).onSuccess {
                        frutaAEditar = null
                    }
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        // La actualización ahora es automática vía Flow
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VerduritasBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // Hero Section
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFE8DEFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory, null, tint = VerduritasPrimary, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Inventario", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                    Text("Controla tus productos frescos sin esfuerzo.", fontSize = 14.sp, color = Color(0xFF494455))
                }
            }
        }

        // Registration Form
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = VerduritasSurface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFFCAC3D8).copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddCircle, null, tint = VerduritasPrimary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Agregar nuevo alimento", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VerduritasPrimary)
                        }
                        
                        IconButton(onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
                            Icon(Icons.Default.CameraAlt, "Escanear", tint = VerduritasSecondary)
                        }
                    }

                    // Name Field
                    VerduritasInputField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre del alimento",
                        placeholder = "Ej. Zanahorias orgánicas",
                        icon = Icons.Default.Restaurant
                    )

                    // Quantity and Expiration Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VerduritasInputField(
                            value = cantidad,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) cantidad = newValue
                            },
                            label = "Cantidad",
                            placeholder = "0",
                            isNumeric = true,
                            modifier = Modifier.weight(1f)
                        )
                        VerduritasInputField(
                            value = fechaCaducidad,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() || it == '-' }) {
                                    fechaCaducidad = newValue
                                }
                            },
                            label = "Caducidad",
                            placeholder = "YYYY-MM-DD",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Storage Location
                    Column {
                        Text("Lugar de almacenamiento", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF494455))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StorageOption(
                                icon = Icons.Default.AcUnit,
                                label = "Refri",
                                selected = storageLocation == "Refri",
                                color = VerduritasPrimary,
                                modifier = Modifier.weight(1f)
                            ) { storageLocation = "Refri" }
                            
                            StorageOption(
                                icon = Icons.Default.Kitchen,
                                label = "Alacena",
                                selected = storageLocation == "Alacena",
                                color = VerduritasPrimary,
                                modifier = Modifier.weight(1f)
                            ) { storageLocation = "Alacena" }
                        }
                    }

                    // Ripeness Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estado de madurez", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF494455))
                            Surface(
                                color = VerduritasSecondary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (ripeness < 33) "FRESCO" else if (ripeness < 66) "MADURO" else "MUY MADURO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerduritasSecondary
                                )
                            }
                        }
                        Slider(
                            value = ripeness,
                            onValueChange = { ripeness = it },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = VerduritasPrimary,
                                activeTrackColor = VerduritasPrimary,
                                inactiveTrackColor = Color(0xFFE4E2E1)
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val cantInt = cantidad.toIntOrNull() ?: 0
                            val frutaNueva = Fruta(
                                nombre = nombre, 
                                cantidad = cantInt,
                                fechaCaducidad = fechaCaducidad,
                                lugarAlmacenamiento = storageLocation
                            )
                            coroutineScope.launch {
                                val res = offlineRepo.guardarFruta(frutaNueva)
                                res.onSuccess { exito ->
                                    if (exito) {
                                        nombre = ""; cantidad = ""; fechaCaducidad = ""
                                    } else {
                                        android.widget.Toast.makeText(context, "Fallo al registrar", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }.onFailure { error ->
                                    if (error.message == "401") {
                                        KtorClient.onSessionExpired?.invoke()
                                    } else {
                                        android.widget.Toast.makeText(context, "Guardado localmente", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEFF), contentColor = VerduritasPrimary)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar alimento", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Recently Added Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("Agregados recientemente", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                    Text("Ver todos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VerduritasPrimary)
                }

                OutlinedTextField(
                    value = queryFruta,
                    onValueChange = { queryFruta = it },
                    label = { Text("Buscar alimento...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
            }
        }

        val filteredFrutas = historialDeFrutas.filter { 
            it.nombre.contains(queryFruta, ignoreCase = true) 
        }

        items(filteredFrutas) { fruta ->
            FoodItemCard(
                name = fruta.nombre, 
                quantity = fruta.cantidad, 
                color = VerduritasPrimary,
                fechaCaducidad = fruta.fechaCaducidad,
                lugar = fruta.lugarAlmacenamiento,
                onDelete = {
                    coroutineScope.launch {
                        android.widget.Toast.makeText(context, "Eliminando...", android.widget.Toast.LENGTH_SHORT).show()
                        val res = offlineRepo.eliminarFruta(fruta.localId, fruta.id)
                        res.onSuccess {
                            android.widget.Toast.makeText(context, "Eliminado con éxito", android.widget.Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            android.widget.Toast.makeText(context, "Error al eliminar: ${it.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onEdit = { frutaAEditar = fruta }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun VerduritasInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector? = null,
    isNumeric: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF494455))
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFCAC3D8)) },
            leadingIcon = icon?.let { { Icon(it, null, tint = Color(0xFF7A7487)) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFCF9F8),
                unfocusedContainerColor = Color(0xFFFCF9F8),
                focusedIndicatorColor = Color(0xFF632CE5),
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun StorageOption(icon: ImageVector, label: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) color.copy(alpha = 0.1f) else Color.White,
        border = BorderStroke(2.dp, if (selected) color else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (selected) color else Color(0xFF7A7487))
            Spacer(Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = if (selected) color else Color(0xFF494455))
        }
    }
}

@Composable
fun FoodItemCard(name: String, quantity: Int, color: Color, fechaCaducidad: String, lugar: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    // Lógica de semáforo compatible con API 24
    var esUrgente by remember(fechaCaducidad) { mutableStateOf(false) }
    var diasRestantes by remember(fechaCaducidad) { mutableLongStateOf(-1L) }

    LaunchedEffect(fechaCaducidad) {
        try {
            if (fechaCaducidad.isNotBlank()) {
                val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val fechaFin = format.parse(fechaCaducidad)
                if (fechaFin != null) {
                    val diff = fechaFin.time - System.currentTimeMillis()
                    diasRestantes = diff / (1000 * 60 * 60 * 24)
                    esUrgente = diasRestantes <= 3
                }
            }
        } catch (e: Exception) {
            esUrgente = name.contains("!") || quantity < 1
        }
    }

    val colorAlerta = if (esUrgente) Color(0xFFBA1A1A) else Color(0xFF006E2A)
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (esUrgente) Color(0xFFFFEBEE) else Color(0xFFF6F3F2)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (esUrgente) Color(0xFFFFCDD2) else Color.White, 
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (esUrgente) Icons.Default.PriorityHigh else Icons.Default.Kitchen, 
                    null, 
                    tint = colorAlerta
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (esUrgente) colorAlerta else Color.Black)
                    Surface(color = if (esUrgente) Color(0xFFFFEBEE) else Color(0xFF69FF87), shape = CircleShape) {
                        Text(
                            text = if (esUrgente) "¡CADUCA PRONTO!" else "Fresco", 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            color = colorAlerta
                        )
                    }
                }
                Text("$quantity unidades • $lugar", fontSize = 14.sp, color = Color(0xFF494455))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule, 
                        contentDescription = null, 
                        tint = colorAlerta, 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (diasRestantes >= 0) "Quedan $diasRestantes días" else if (fechaCaducidad.isBlank()) "Sin fecha" else "¡EXPIRADO!", 
                        fontSize = 12.sp, 
                        color = colorAlerta
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFF7A7487))
                }
            }
        }
    }
}

@Composable
fun EditarFrutaDialog(fruta: Fruta, onDismiss: () -> Unit, onUpdate: (Int, Fruta) -> Unit) {
    var nombreEdit by remember { mutableStateOf(fruta.nombre) }
    var cantidadEdit by remember { mutableStateOf(fruta.cantidad.toString()) }
    var fechaEdit by remember { mutableStateOf(fruta.fechaCaducidad) }
    var lugarEdit by remember { mutableStateOf(fruta.lugarAlmacenamiento) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Alimento", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = nombreEdit, onValueChange = { nombreEdit = it }, label = { Text("Nombre") })
                OutlinedTextField(
                    value = cantidadEdit, 
                    onValueChange = { if (it.all { c -> c.isDigit() }) cantidadEdit = it }, 
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = fechaEdit, 
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '-' }) fechaEdit = it }, 
                    label = { Text("Caducidad (YYYY-MM-DD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = lugarEdit == "Refri",
                        onClick = { lugarEdit = "Refri" },
                        label = { Text("Refri") }
                    )
                    FilterChip(
                        selected = lugarEdit == "Alacena",
                        onClick = { lugarEdit = "Alacena" },
                        label = { Text("Alacena") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onUpdate(fruta.id, fruta.copy(
                    nombre = nombreEdit, 
                    cantidad = cantidadEdit.toIntOrNull() ?: 0,
                    fechaCaducidad = fechaEdit,
                    lugarAlmacenamiento = lugarEdit
                ))
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
