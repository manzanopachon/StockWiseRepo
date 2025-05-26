package com.mauricio.helloworld.screens.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mauricio.helloworld.Pedido
import com.mauricio.helloworld.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun actualizarEstado(
    codigoPedido: String,
    estado: String,
    context: Context,
    onSuccess: (descuentoAplicado: Boolean) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val pedido = RetrofitClient.apiService.buscarPedidoPorCodigo(codigoPedido)
            RetrofitClient.apiService.cambiarEstado(pedido.id, estado)

            val ingredientesDescontados = (estado == "FINALIZADO")

            withContext(Dispatchers.Main) {
                if (ingredientesDescontados) {
                    Toast.makeText(context, "✅ Ingredientes descontados", Toast.LENGTH_SHORT).show()
                }
                onSuccess(ingredientesDescontados)
            }

        } catch (e: Exception) {
            Log.e("Estado", "❌ Error al cambiar estado", e)
        }
    }
}

suspend fun recargarPedidos(restauranteId: Long, onResult: (List<Pedido>) -> Unit) {
    val nuevosPedidos = RetrofitClient.apiService.obtenerTodosLosPedidos(restauranteId)
    onResult(nuevosPedidos)
}
