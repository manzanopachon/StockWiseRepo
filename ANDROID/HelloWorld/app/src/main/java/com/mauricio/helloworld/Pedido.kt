package com.mauricio.helloworld

data class Pedido(
    val id: Long,
    val numeroMesa: Int,
    val restauranteId: Long,
    val platos: List<Long>,
    val nombresPlatos: List<String>,
    val total: Double,
    val fechaHora: String,
    val codigoPedido: String,
    val estadoPedido: String
)
