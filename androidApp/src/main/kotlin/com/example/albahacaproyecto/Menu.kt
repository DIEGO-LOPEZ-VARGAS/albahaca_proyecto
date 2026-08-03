package com.example.albahacaproyecto

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(onCerrarSesion: () -> Unit = {}) {
    var seccionActual by remember { mutableStateOf("menu_principal") }

    val VerduritasPrimary = MaterialTheme.colorScheme.primary
    val VerduritasSecondary = MaterialTheme.colorScheme.secondary
    val VerduritasBackground = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    if (seccionActual != "menu_principal") {
                        Text(
                            "⬅️ Volver",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerduritasPrimary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { seccionActual = "menu_principal" }
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Eco, null, tint = VerduritasPrimary, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Albahaca",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerduritasPrimary
                        )
                    }

                    IconButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = VerduritasPrimary
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (seccionActual == "menu_principal") {
                VerduritasBottomNav()
            }
        },
        containerColor = VerduritasBackground
    ) { paddingValues ->
        AnimatedContent(
            targetState = seccionActual,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) + slideInHorizontally { it } togetherWith
                        fadeOut(animationSpec = tween(500)) + slideOutHorizontally { -it }
            },
            label = "ScreenTransition"
        ) { targetSection ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(VerduritasPrimary.copy(alpha = 0.05f), VerduritasBackground),
                            radius = 1500f
                        )
                    )
            ) {
                when (targetSection) {
                    "menu_principal" -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    val displayUserName = KtorClient.userName ?: "Alex"
                                    Text("Buenos días, $displayUserName", fontSize = 14.sp, color = Color(0xFF494455))
                                    Text("¡Hola de nuevo!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                                }
                            }

                            // 🔥 CUADRO DE UBICACIÓN Y GEOLOCALIZACIÓN
                            item {
                                UbicacionWidget()
                            }

                            // Bento Grid
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        VerduritasCard(
                                            icon = Icons.Default.Inventory,
                                            title = "Registrar ingredientes",
                                            category = "Inventario",
                                            color = VerduritasPrimary,
                                            modifier = Modifier.weight(1f)
                                        ) { seccionActual = "ver_guardar_verduras" }

                                        VerduritasCard(
                                            icon = Icons.Default.RestaurantMenu,
                                            title = "Crear receta",
                                            category = "Cocina",
                                            color = VerduritasSecondary,
                                            modifier = Modifier.weight(1f)
                                        ) { seccionActual = "crear_recetas" }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        VerduritasCard(
                                            icon = Icons.Default.ShoppingBasket,
                                            title = "Próxima compra",
                                            category = "Planificación",
                                            color = VerduritasPrimary,
                                            modifier = Modifier.weight(1f)
                                        ) { seccionActual = "lista_compras" }

                                        // Tarjeta vacía para mantener el grid alineado
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            // Tip del día
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Sugerencia fresca", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.AutoAwesome, null, tint = VerduritasSecondary)
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Card(
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Box {
                                            // Placeholder for image
                                            Box(modifier = Modifier.fillMaxSize().background(Color.Gray))

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                        )
                                                    )
                                            )
                                            Column(
                                                modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                                            ) {
                                                Surface(
                                                    color = VerduritasSecondary.copy(alpha = 0.2f),
                                                    shape = CircleShape,
                                                    contentColor = Color.White
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Verified, null, modifier = Modifier.size(14.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("TIP DEL DÍA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text("Conserva tus hojas verdes", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    "Envuelve la lechuga y espinacas en papel absorbente antes de refrigerar.",
                                                    color = Color.White.copy(alpha = 0.8f),
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Eco, null, tint = VerduritasSecondary, modifier = Modifier.size(24.dp).alpha(0.3f))
                                    Spacer(Modifier.width(16.dp))
                                    Box(Modifier.size(8.dp).background(VerduritasPrimary.copy(alpha = 0.3f), CircleShape))
                                    Spacer(Modifier.width(16.dp))
                                    Icon(Icons.Default.Spa, null, tint = VerduritasSecondary, modifier = Modifier.size(24.dp).alpha(0.3f))
                                }
                            }
                        }
                    }

                    "ver_guardar_verduras" -> FrutaFormView()
                    "crear_recetas"        -> RecetaView()
                    "lista_compras"        -> ListaComprasScreen()
                    "productos_rama2"      -> ProductosScreen()
                    "recetas_guardadas"    -> RecetaLocalView()
                }
            }
        }
    }
}

@Composable
fun VerduritasCard(
    icon: ImageVector,
    title: String,
    category: String,
    color: Color,
    hasNotification: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Surface(
        onClick = {
            scope.launch {
                scale.animateTo(0.95f, animationSpec = tween(100))
                scale.animateTo(1f, animationSpec = tween(100))
                onClick()
            }
        },
        modifier = modifier
            .height(180.dp)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(color.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            // Background decorative icon with animation
            val rotation = rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing))
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color.copy(alpha = 0.03f),
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .rotate(rotation.value)
            )

            Column {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(color.copy(alpha = 0.2f), color.copy(alpha = 0.05f))),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
                    if (hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color.Red, CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    category.uppercase(), 
                    fontSize = 12.sp, 
                    color = color, 
                    fontWeight = FontWeight.ExtraBold, 
                    letterSpacing = 2.sp
                )
                Text(
                    title, 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF1B1C1C), 
                    lineHeight = 26.sp
                )
            }
        }
    }
}

@Composable
fun VerduritasBottomNav() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, true)
            BottomNavItem(Icons.Default.Search, false)
            BottomNavItem(Icons.Default.Person, false)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, selected: Boolean) {
    IconButton(
        onClick = {},
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) Color(0xFF632CE5).copy(alpha = 0.1f) else Color.Transparent)
            .padding(12.dp)
    ) {
        Icon(icon, null, tint = if (selected) Color(0xFF632CE5) else Color.Gray)
    }
}


@Composable
fun RecetaLocalView() {
    val local = RecetaStorage.ultimaRecetaGuardada
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        if (local != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Última Receta Local", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Título: ${local.titulo}", fontWeight = FontWeight.Bold)
                Text("Ingredientes: ${local.ingredientes}")
                Text("Pasos: ${local.pasos}")
            }
        } else {
            Text("No hay recetas en el almacenamiento local.")
        }
    }
}

// ── Componentes reutilizables (sin cambios) ───────────────────────────────────

@Composable
fun TarjetaMenu(
    icono: String,
    titulo: String,
    sub: String,
    colorFondo: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(46.dp).background(color = colorFondo, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(text = icono, fontSize = 22.sp) }
            Column {
                Text(text = titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), lineHeight = 18.sp)
                Text(text = sub, fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
        }
    }
}

@Composable
fun VistaGenerica(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}