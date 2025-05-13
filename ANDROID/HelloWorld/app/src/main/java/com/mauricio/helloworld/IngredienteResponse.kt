package com.mauricio.helloworld

data class IngredienteResponse(
    val id: Long,
    val nombre: String,
    val cantidadStock: Double,
    val unidadMedida: String,
    val prioridadBaja: Double,
    val prioridadMedia: Double,
    val prioridadAlta: Double,
    val proveedor: String,
    val fotoUrl: String,
    val color: String
)
