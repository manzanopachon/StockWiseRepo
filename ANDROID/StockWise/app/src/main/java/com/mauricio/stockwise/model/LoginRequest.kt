package com.mauricio.stockwise.model

data class LoginRequest(
    val correo: String,
    val contraseña: String
)