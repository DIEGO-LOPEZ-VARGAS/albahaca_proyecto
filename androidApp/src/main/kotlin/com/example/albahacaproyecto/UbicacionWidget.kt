package com.example.albahacaproyecto

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

@Composable
fun UbicacionWidget() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var direccionTexto by remember { mutableStateOf("Presiona el botón para obtener tu ubicación") }
    var cargandoUbicacion by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val concedido = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        tienePermiso = concedido
        if (concedido) {
            cargandoUbicacion = true
            obtenerMunicipioEstadoCP(context, fusedLocationClient) { resultado ->
                direccionTexto = resultado
                cargandoUbicacion = false
            }
        } else {
            direccionTexto = "Permiso denegado por el usuario"
            cargandoUbicacion = false
            Toast.makeText(context, "Se requiere permiso de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3EDF7),
        border = BorderStroke(1.dp, Color(0xFF632CE5).copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF632CE5),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Mi Ubicación Actual",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1C1C)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (cargandoUbicacion) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF632CE5))
                    Spacer(Modifier.height(6.dp))
                    Text("Obteniendo datos...", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                Text(
                    text = direccionTexto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF494455),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    cargandoUbicacion = true
                    if (tienePermiso) {
                        obtenerMunicipioEstadoCP(context, fusedLocationClient) { resultado ->
                            direccionTexto = resultado
                            cargandoUbicacion = false
                        }
                    } else {
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF632CE5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (tienePermiso) "Actualizar Dirección" else "Permitir Ubicación")
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun obtenerMunicipioEstadoCP(
    context: Context,
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient,
    onResultado: (String) -> Unit
) {
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            if (location != null) {
                convertirAMunicipioEstadoCP(context, location.latitude, location.longitude, onResultado)
            } else {
                fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                    if (lastLoc != null) {
                        convertirAMunicipioEstadoCP(context, lastLoc.latitude, lastLoc.longitude, onResultado)
                    } else {
                        onResultado("Verifica tener activada la 'Ubicación / GPS'")
                    }
                }
            }
        }
        .addOnFailureListener {
            onResultado("Error con el sensor GPS")
        }
}

private fun convertirAMunicipioEstadoCP(
    context: Context,
    lat: Double,
    lon: Double,
    onResultado: (String) -> Unit
) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val formatearResultado: (android.location.Address) -> String = { addr ->
            val municipio = addr.locality ?: addr.subAdminArea ?: ""
            val estado = addr.adminArea ?: ""
            val cp = if (!addr.postalCode.isNullOrBlank()) "C.P. ${addr.postalCode}" else ""

            val elementos = listOf(municipio, estado, cp).filter { it.isNotBlank() }

            if (elementos.isNotEmpty()) {
                "📍 " + elementos.joinToString(", ")
            } else {
                "Lat: $lat\nLon: $lon"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                if (list.isNotEmpty()) {
                    onResultado(formatearResultado(list[0]))
                } else {
                    onResultado("Lat: $lat\nLon: $lon")
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (!list.isNullOrEmpty()) {
                onResultado(formatearResultado(list[0]))
            } else {
                onResultado("Lat: $lat\nLon: $lon")
            }
        }
    } catch (e: Exception) {
        onResultado("Lat: $lat\nLon: $lon")
    }
}