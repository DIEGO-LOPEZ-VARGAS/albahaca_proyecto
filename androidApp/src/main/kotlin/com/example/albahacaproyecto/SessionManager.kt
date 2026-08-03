package com.example.albahacaproyecto

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("albahaca_session", Context.MODE_PRIVATE)

    fun saveSession(token: String?, name: String?) {
        prefs.edit().apply {
            putString("auth_token", token)
            putString("user_name", name)
            apply()
        }
        KtorClient.sessionToken = token
        KtorClient.userName = name
    }

    fun getToken(): String? = prefs.getString("auth_token", null)
    fun getUserName(): String? = prefs.getString("user_name", null)

    fun clearSession() {
        prefs.edit().clear().apply()
        KtorClient.sessionToken = null
        KtorClient.userName = null
    }
}
