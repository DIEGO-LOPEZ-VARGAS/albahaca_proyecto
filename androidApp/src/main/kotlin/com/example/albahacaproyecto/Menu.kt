package com.example.albahacaproyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@Composable
fun MainMenuScreen() {
    // ── ÚNICA LÍNEA NUEVA: agregar "railway_status" al conjunto de secciones ──
    var seccionActual by remember { mutableStateOf("menu_principal") }

    val verdePrincipal          = Color(0xFF2E5A39)
    val verdeFondoIcono         = Color(0xFFE8F0EA)
    val grisTextoSecundario     = Color(0xFF6B7280)
    val rojoAlertaFondo         = Color(0xFFFDE8E8)
    val rojoAlertaTexto         = Color(0xFF9B1C1C)

    val verdurasPorCaducar = listOf(
        Pair("🌿 Albahaca Fresca", "Caduca en: Hoy"),
        Pair("🍅 Tomates Bola",    "Caduca en: 2 días"),
        Pair("🥦 Brócoli",         "Caduca en: 3 días")
    )

    Scaffold(
        topBar = {
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
                        color = verdePrincipal,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable { seccionActual = "menu_principal" }
                    )
                }
                Text(
                    "Albahaca",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (seccionActual) {

                "menu_principal" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text("¡Hola, Administrador!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                Text("¿Qué deseas gestionar hoy?", fontSize = 14.sp, color = grisTextoSecundario)
                            }
                        }

                        // ── Fila 1: Verduras y Recetas ────────────────────
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TarjetaMenu("🥦", "Ver / Guardar\nVerduras",  "Inventario actual",     verdeFondoIcono, Modifier.weight(1f)) { seccionActual = "ver_guardar_verduras" }
                                TarjetaMenu("🍳", "Crear\nRecetas",           "Combina ingredientes",  verdeFondoIcono, Modifier.weight(1f)) { seccionActual = "crear_recetas" }
                            }
                        }

                        // ── Fila 2: Recetas guardadas + NUEVA tarjeta Rama 3 ──
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TarjetaMenu("📖", "Mis Recetas\nGuardadas", "Ver tus creaciones", verdeFondoIcono, Modifier.weight(1f)) { seccionActual = "recetas_guardadas" }

                                // ── TARJETA NUEVA – Rama 3 ─────────────────
                                TarjetaMenu("🌐", "Ruteo\nRailway", "Estado del servidor", verdeFondoIcono, Modifier.weight(1f)) { seccionActual = "railway_status" }
                            }

                        }
                        // ── Fila 3: Productos Rama 2 ──────────────────────────────
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TarjetaMenu(
                                    "📦",
                                    "Productos\nRama 2",
                                    "Despensa y refrigerador",
                                    verdeFondoIcono,
                                    Modifier.weight(1f)
                                ) { seccionActual = "productos_rama2" }
                            }
                        }

                        item {
                            Text(
                                "⚠️ Próximos a Caducar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                            )
                        }

                        items(verdurasPorCaducar) { verdura ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = rojoAlertaFondo, shape = RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(verdura.first, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                                Text(verdura.second, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = rojoAlertaTexto)
                            }
                        }
                    }
                }

                "ver_guardar_verduras" -> VistaGenerica("🥦 Apartado de Verduras")
                "crear_recetas"        -> VistaGenerica("🍳 Creador de Recetas")
                "recetas_guardadas"    -> VistaGenerica("📖 Recetario Local")

                // ── NUEVA sección – muestra la pantalla de la Rama 3 ─────
                "railway_status"       -> RailwayStatusScreen()
                "productos_rama2"      -> ProductosScreen()
            }
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