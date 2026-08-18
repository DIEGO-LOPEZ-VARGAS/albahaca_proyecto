package com.example.albahacaproyecto

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidBiometricHelper(private val context: Context) {

<<<<<<< Updated upstream
    // 🔥 Nombre restaurado a lanzarLectorHuella para que coincida con tu loggin.kt
=======
    // Función privada para extraer el FragmentActivity sin importar los wrappers de Compose
    private fun findFragmentActivity(): FragmentActivity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is FragmentActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

>>>>>>> Stashed changes
    suspend fun lanzarLectorHuella(): Boolean = suspendCancellableCoroutine { continuation ->
        val biometricManager = BiometricManager.from(context)

        // Permite Huella y Reconocimiento Facial
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

        if (biometricManager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            if (continuation.isActive) continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        // Obtención segura del FragmentActivity
        val activity = findFragmentActivity()
        if (activity == null) {
            if (continuation.isActive) continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (continuation.isActive) continuation.resume(false)
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Escanee su rostro o coloque su huella para acceder")
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(authenticators)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}