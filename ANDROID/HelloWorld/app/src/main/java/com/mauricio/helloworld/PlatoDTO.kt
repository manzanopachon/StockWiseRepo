package com.mauricio.helloworld
//data class CategoriaDTO(val nombre: String)

data class IngredienteCantidadDTO(
    val ingredienteId: Long,
    val cantidad: Double
)

data class PlatoDTO(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: CategoriaDTO,
    val restauranteId: Long,
    val ingredientes: List<IngredienteCantidadDTO>
)

