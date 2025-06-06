package com.mauricio.stockwise.model

data class LlamadaCamarero(
    val id: Long,
    val restauranteId: Long,
    val mesaId: Int,
    val horaLlamada: String,
    val atendida: Boolean
)
