package com.mauricio.helloworld.model

data class LoginResponse(
    val mensaje: String,
    val empleadoId: Long,
    val requiereCodigo: Boolean,
    val nombreEmpleado: String?,  // Nombre del empleado
    val restauranteId: Long
)
