package com.mauricio.helloworld

data class Restaurante(val id: Long)

data class Empleado(
    val id: Long?,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val contraseña: String,
    val puestoTrabajo: String,
    val restaurante: Restaurante,
    val codigoValidacion: String,
    val validado: Boolean // Para que el LoginScreen pueda comprobarlo
)

