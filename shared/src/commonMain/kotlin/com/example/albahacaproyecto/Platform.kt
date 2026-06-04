package com.example.albahacaproyecto

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform