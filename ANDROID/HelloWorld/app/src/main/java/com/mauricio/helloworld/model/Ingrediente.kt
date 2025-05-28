package com.mauricio.helloworld.model

data class Ingrediente(
    val id: Long,
    val nombre: String,
    val unidadMedida: String,
    val cantidadStock: Double,
    val prioridadBaja: Double,
    val prioridadMedia: Double,
    val prioridadAlta: Double,
    val proveedor: String?,
    val fotoUrl: String?,
    val restauranteId: Long
)
