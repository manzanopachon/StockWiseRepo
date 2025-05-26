package com.mauricio.helloworld

data class DetallePlato(
    val nombre: String,
    val precio: Double
)


data class Pedido(
    val id: Long,
    val numeroMesa: Int,
    val restauranteId: Long,
    val platos: List<Long>,
    val detallesPlatos: List<DetallePlato>,
    val total: Double,
    val fechaHora: String,
    val codigoPedido: String,
    val estadoPedido: String
)
