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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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

    val coroutineScope = rememberCoroutineScope()
    val apiCliente = remember { FrutaApiClient() }
    var historialDeFrutas by remember { mutableStateOf<List<Fruta>>(emptyList()) }

    LaunchedEffect(Unit) {
        historialDeFrutas = apiCliente.obtenerFrutas()
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
                    Text("Track your fresh produce effortlessly.", fontSize = 14.sp, color = Color(0xFF494455))
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddCircle, null, tint = VerduritasPrimary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add New Food", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VerduritasPrimary)
                    }

                    // Name Field
                    VerduritasInputField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Food Name",
                        placeholder = "e.g. Organic Carrots",
                        icon = Icons.Default.Restaurant
                    )

                    // Quantity and Expiration Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VerduritasInputField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = "Cantidad",
                            placeholder = "0",
                            modifier = Modifier.weight(1f)
                        )
                        VerduritasInputField(
                            value = fechaCaducidad,
                            onValueChange = { fechaCaducidad = it },
                            label = "Caducidad",
                            placeholder = "YYYY-MM-DD",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Storage Location
                    Column {
                        Text("Storage Location", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF494455))
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
                            Text("Ripeness Status", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF494455))
                            Surface(
                                color = VerduritasSecondary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (ripeness < 33) "FRESH" else if (ripeness < 66) "RIPE" else "OVERRIPE",
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
                            val frutaNueva = Fruta(nombre, cantInt)
                            coroutineScope.launch {
                                val exito = apiCliente.enviarFruta(frutaNueva)
                                if (exito) {
                                    historialDeFrutas = apiCliente.obtenerFrutas()
                                    nombre = ""; cantidad = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEFF), contentColor = VerduritasPrimary)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Register Food", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Recently Added Section
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("Recently Added", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                Text("View All", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VerduritasPrimary)
            }
        }

        items(historialDeFrutas) { fruta ->
            FoodItemCard(name = fruta.nombre, quantity = fruta.cantidad, color = VerduritasPrimary)
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
fun FoodItemCard(name: String, quantity: Int, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF6F3F2)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).background(Color.White, RoundedCornerShape(12.dp)))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(color = Color(0xFF69FF87), shape = CircleShape) {
                        Text("Fresh", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("$quantity unidades • Refri", fontSize = 14.sp, color = Color(0xFF494455))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Expires in 3 days", fontSize = 12.sp, color = Color(0xFFBA1A1A))
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFF7A7487))
            }
        }
    }
}
