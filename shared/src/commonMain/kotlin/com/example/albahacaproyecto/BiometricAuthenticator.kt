package com.example.albahacaproyecto

/**
 * Clase común para simular el puente de autenticación en el proyecto
 */
class BiometricAuthenticator {
    suspend fun autenticarConHuella(): Boolean {
        // Por defecto en plataformas comunes devuelve true para no bloquear el flujo
        return true
    }
}