package com.example.albahacaproyecto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminMenuScreen(onCerrarSesion: () -> Unit) {
    val grisTexto = Color(0xFF6B7280)

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
                    text = "⚙️ Panel de Administración",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Estado del Servidor ──────────────────────────────────────────
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Estado del Servidor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Estatus API:", fontSize = 14.sp, color = grisTexto)
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🟢 En línea (200 OK)",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Latencia:", fontSize = 14.sp, color = grisTexto)
                            Text("45 ms", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ── Métricas de Usuarios y BD ──────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaMetricaAdmin(
                        titulo = "👥 Usuarios",
                        valor = "128",
                        subtitulo = "Registrados",
                        fondo = Color(0xFFE3F2FD),
                        colorTexto = Color(0xFF1565C0),
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetricaAdmin(
                        titulo = "💾 BD Room",
                        valor = "Activa",
                        subtitulo = "Sincronizada",
                        fondo = Color(0xFFFFFBEB),
                        colorTexto = Color(0xFF92400E),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Resumen General ──────────────────────────────────────────────
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumen General",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("• Módulo de login biométrico activo.", fontSize = 13.sp, color = grisTexto)
                        Text("• Alertas de caducidad locales: Operativas.", fontSize = 13.sp, color = grisTexto)
                        Text("• Base de datos SQLite/Room: Sin errores.", fontSize = 13.sp, color = grisTexto)
                    }
                }
            }

            // ── Botón Cerrar Sesión ───────────────────────────────────────────
            item {
                Button(
                    onClick = onCerrarSesion,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cerrar Sesión de Admin",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TarjetaMetricaAdmin(
    titulo: String,
    valor: String,
    subtitulo: String,
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = valor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorTexto
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitulo,
                fontSize = 11.sp,
                color = colorTexto.copy(alpha = 0.7f)
            )
        }
    }
}