package com.example.albahacaproyecto

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

val VerduritasPrimary = Color(0xFF632CE5)
val VerduritasSecondary = Color(0xFF006E2A)
val VerduritasBackground = Color(0xFFFCF9F8)
val VerduritasSurface = Color(0xFFFFFFFF)
val VerduritasOnSurfaceVariant = Color(0xFF494455)
val VerduritasOutline = Color(0xFF7A7487)

// Función auxiliar para obtener la FragmentActivity válida requerida por BiometricPrompt
fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun DefinitiveLoginScreen(onLoginExitoso: (Boolean) -> Unit = {}) {
    var screenState by remember { mutableStateOf("login") }

    if (screenState == "login") {
        LoginContent(
            onLoginExitoso = onLoginExitoso,
            onGoToRegister = { screenState = "register" }
        )
    } else {
        RegisterContent(
            onBackToLogin = { screenState = "login" }
        )
    }
}

@Composable
fun LoginContent(onLoginExitoso: (Boolean) -> Unit, onGoToRegister: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
<<<<<<< Updated upstream
=======
    val sessionManager = remember { SessionManager(context) }
>>>>>>> Stashed changes

    // Cargar token guardado si existe
    val savedToken = remember { sessionManager.getToken() }

<<<<<<< Updated upstream
=======
    LaunchedEffect(Unit) {
        if (!savedToken.isNullOrEmpty()) {
            val activity = context.findFragmentActivity()
            if (activity != null) {
                val biometricHelper = AndroidBiometricHelper(activity)
                val exito = biometricHelper.lanzarLectorHuella()
                if (exito) {
                    Toast.makeText(context, "¡Acceso concedido!", Toast.LENGTH_SHORT).show()
                    val savedUser = sessionManager.getUserName() ?: ""
                    val esAdmin = savedUser.lowercase().trim() == "admin"
                    onLoginExitoso(esAdmin)
                }
            }
        }
    }

>>>>>>> Stashed changes
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VerduritasPrimary.copy(alpha = 0.05f),
                        VerduritasBackground
                    ),
                    radius = 2000f
                )
            )
    ) {
        FloatingIcon(Icons.Default.Egg, VerduritasSecondary, 0.2f, 80.dp, Modifier.align(Alignment.TopStart).padding(40.dp))
        FloatingIcon(Icons.Default.Eco, VerduritasSecondary, 0.1f, 60.dp, Modifier.align(Alignment.CenterStart).offset(x = (-20).dp))
        FloatingIcon(Icons.Default.Spa, VerduritasPrimary, 0.2f, 90.dp, Modifier.align(Alignment.BottomEnd).padding(bottom = 80.dp, end = 20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = VerduritasSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Albahaca",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerduritasPrimary
                )
            }

            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = VerduritasSurface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, VerduritasOutline.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(VerduritasPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = VerduritasPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Bienvenido de nuevo",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1C1C)
                    )
                    Text(
                        text = "Tu dosis diaria de frescura te espera.",
                        fontSize = 16.sp,
                        color = VerduritasOnSurfaceVariant
                    )

                    Spacer(Modifier.height(32.dp))

                    VerduritasTextField(
                        value = usuario,
                        onValueChange = { usuario = it },
                        label = "Usuario o Correo",
                        placeholder = "Ingresa tu nombre o email",
                        leadingIcon = Icons.Default.AccountCircle
                    )

                    Spacer(Modifier.height(16.dp))

                    VerduritasTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
<<<<<<< Updated upstream

                                // Llamamos a RailwayKtorService y recibimos el error detallado (o null)
                                val errorLogin = RailwayKtorService.loginUsuario(context, usuario, contrasena)

                                if (errorLogin == null) {
                                    // Guardar la sesión de forma persistente (Token y Nombre)
                                    sessionManager.saveSession(KtorClient.sessionToken, KtorClient.userName)
                                    // Cambia de pantalla (Ejecuta la lambda de éxito)
                                    onLoginExitoso()
                                } else {
                                    // Si regresa falso, asumimos credenciales incorrectas o error en el servidor
                                    Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
=======
                                val errorLogin = RailwayKtorService.loginUsuario(context, usuario, contrasena)

                                if (errorLogin == null) {
                                    sessionManager.saveSession(KtorClient.sessionToken, KtorClient.userName)
                                    val esAdmin = usuario.lowercase().trim() == "admin" || (KtorClient.userName?.lowercase()?.trim() == "admin")
                                    onLoginExitoso(esAdmin)
                                } else {
                                    Toast.makeText(context, errorLogin, Toast.LENGTH_LONG).show()
>>>>>>> Stashed changes
                                }
                                isLoading = false
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = VerduritasPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Botón de Huella Digital Ajustado
                    IconButton(
                        onClick = {
<<<<<<< Updated upstream
=======
                            val activity = context.findFragmentActivity()
                            if (activity == null) {
                                Toast.makeText(context, "Error: Contexto de Activity no válido", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }

                            val biometricHelper = AndroidBiometricHelper(activity)
>>>>>>> Stashed changes
                            coroutineScope.launch {
                                val exito = biometricHelper.lanzarLectorHuella()
                                if (exito) {
                                    Toast.makeText(context, "¡Acceso biométrico concedido!", Toast.LENGTH_SHORT).show()
                                    val userGuardado = sessionManager.getUserName() ?: ""
                                    val esAdmin = userGuardado.lowercase().trim() == "admin"
                                    onLoginExitoso(esAdmin)
                                } else {
                                    Toast.makeText(context, "Autenticación fallida o cancelada", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Acceso biométrico",
                            tint = VerduritasPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = VerduritasOutline.copy(alpha = 0.3f))
                        Text(
                            text = "o continúa con",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontSize = 12.sp,
                            color = VerduritasOnSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = VerduritasOutline.copy(alpha = 0.3f))
                    }

                    Spacer(Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = onGoToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, VerduritasOutline.copy(alpha = 0.3f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Crear cuenta", color = Color(0xFF1B1C1C), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF1B1C1C), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Icon(Icons.Default.SelfImprovement, null, Modifier.size(24.dp).alpha(0.4f), VerduritasPrimary)
                    Icon(Icons.Default.LocalShipping, null, Modifier.size(24.dp).alpha(0.4f), VerduritasSecondary)
                    Icon(Icons.Default.VolunteerActivism, null, Modifier.size(24.dp).alpha(0.4f), VerduritasPrimary)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "© 2024 Verduritas. Cultivando felicidad.",
                    fontSize = 12.sp,
                    color = VerduritasOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RegisterContent(onBackToLogin: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerduritasBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerduritasPrimary
            )
            Text(
                text = "Únete a la frescura de Albahaca",
                fontSize = 16.sp,
                color = VerduritasOnSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = VerduritasSurface,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    VerduritasTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre completo",
                        placeholder = "Ej. Alex González",
                        leadingIcon = Icons.Default.Badge
                    )
                    VerduritasTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo electrónico",
                        placeholder = "correo@ejemplo.com",
                        leadingIcon = Icons.Default.Email
                    )
                    VerduritasTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                val codigo = KtorClient.enviarRegistro(nombre, email, password)
                                if (codigo == 201) {
                                    Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                    onBackToLogin()
                                } else {
                                    Toast.makeText(context, "Error al registrar: $codigo", Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
                            }
                        },
                        enabled = !isLoading && nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerduritasPrimary)
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White)
                        else Text("Registrarse", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onBackToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión", color = VerduritasPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun VerduritasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = VerduritasOnSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = VerduritasOnSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = VerduritasOnSurfaceVariant) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = VerduritasOnSurfaceVariant
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0EDED),
                unfocusedContainerColor = Color(0xFFF0EDED),
                disabledContainerColor = Color(0xFFF0EDED),
                focusedIndicatorColor = VerduritasPrimary,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
fun FloatingIcon(icon: ImageVector, tint: Color, alpha: Float, size: androidx.compose.ui.unit.Dp, modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .offset { IntOffset(0, translateY.toInt()) }
            .rotate(rotate)
    )
}