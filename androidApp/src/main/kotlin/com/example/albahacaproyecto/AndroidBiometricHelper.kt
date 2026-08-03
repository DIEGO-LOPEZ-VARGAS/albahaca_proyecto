package com.example.albahacaproyecto

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidBiometricHelper(private val context: Context) {

    //  Nombre restaurado a lanzarLectorHuella para que coincida con tu loggin.kt
    suspend fun lanzarLectorHuella(): Boolean = suspendCancellableCoroutine { continuation ->
        val biometricManager = BiometricManager.from(context)

        // Permite Huella y Reconocimiento Facial
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

        if (biometricManager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val executor = ContextCompat.getMainExecutor(context)
        val activity = context as? FragmentActivity

        if (activity == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

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