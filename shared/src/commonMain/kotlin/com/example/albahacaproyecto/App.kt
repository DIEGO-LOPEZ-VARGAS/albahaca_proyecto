package com.example.albahacaproyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 1. MODELO DE DATOS COMPARTIDO
data class UsuarioRama1(
    val id: Int,
    val nombre: String,
    val rol: String,
    val status: String
)

@Composable
fun App() {
    val scope = rememberCoroutineScope()

    // 2. ALMACENAMIENTO LOCAL EN MEMORIA
    var listaLocalUsuarios by remember { mutableStateOf(listOf<UsuarioRama1>()) }
    var estadoConexion by remember { mutableStateOf("Sin sincronizar") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)) // Fondo premium estilo el de Diego
            .statusBarsPadding() // <--- TRUCO MAGICO: Empuja todo hacia abajo para que no choque con la hora/batería
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // NOMBRE DE LA APP
        androidx.compose.foundation.text.BasicText(
            text = "Albahaca",
            style = TextStyle(fontSize = 18.sp, color = Color(0xFF4A5568), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // TÍTULO DEL MÓDULO
        androidx.compose.foundation.text.BasicText(
            text = "Sincronización Rama 1",
            style = TextStyle(fontSize = 26.sp, color = Color(0xFF1A202C), fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // INDICADOR DE ESTADO EN TEXTO
        androidx.compose.foundation.text.BasicText(
            text = "Estado: $estadoConexion",
            style = TextStyle(fontSize = 13.sp, color = if (estadoConexion.contains("éxito")) Color(0xFF38A169) else Color(0xFF718096))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN PERSONALIZADO MORADO INSTITUCIONAL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF6B11F4), shape = RoundedCornerShape(14.dp))
                .clickable {
                    scope.launch {
                        try {
                            estadoConexion = "Conectando al servidor Ktor..."
                            delay(1200)

                            val nuevosUsuarios = listOf(
                                UsuarioRama1(1, "Ambar Jezabel", "Rama 1", "Activo"),
                                UsuarioRama1(2, "Diego López", "Rama Administrador", "Offline"),
                                UsuarioRama1(3, "Gabi", "Rama 2", "Activo")
                            )

                            listaLocalUsuarios = nuevosUsuarios
                            estadoConexion = "Sincronizado con éxito (Datos Guardados)"
                        } catch (e: Exception) {
                            estadoConexion = "Error al conectar"
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.text.BasicText(
                text = "Sincronizar y Registrar Datos",
                style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECCIÓN DE REGISTROS LOCALES (Limpio y alineado a la izquierda)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.foundation.text.BasicText(
                text = "📁 Almacenamiento Local Registrado",
                style = TextStyle(fontSize = 15.sp, color = Color(0xFF2D3748), fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LISTA DE TARJETAS ESTILIZADAS
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaLocalUsuarios) { usuario ->
                val esActivo = usuario.status.lowercase() == "activo"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Círculo indicador de estatus
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (esActivo) Color(0xFF48BB78) else Color(0xFFA0AEC0), shape = CircleShape)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    // Textos de la tarjeta
                    Column(modifier = Modifier.weight(1f)) {
                        androidx.compose.foundation.text.BasicText(
                            text = usuario.nombre,
                            style = TextStyle(fontSize = 16.sp, color = Color(0xFF1A202C), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        androidx.compose.foundation.text.BasicText(
                            text = "Rol: ${usuario.rol}",
                            style = TextStyle(fontSize = 13.sp, color = Color(0xFF718096))
                        )
                    }

                    // Etiqueta visual de estatus
                    Box(
                        modifier = Modifier
                            .background(
                                if (esActivo) Color(0xFFE6FFFA) else Color(0xFFEDF2F7),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        androidx.compose.foundation.text.BasicText(
                            text = usuario.status,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = if (esActivo) Color(0xFF234E52) else Color(0xFF4A5568),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}