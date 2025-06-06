package com.mauricio.stockwise.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.model.Pedido
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.screens.utils.formatearFecha
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetallePedidoScreen(navController: NavController, codigoPedido: String) {
    var pedido by remember { mutableStateOf<Pedido?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val api = RetrofitClient.apiService

    LaunchedEffect(codigoPedido) {
        try {
            pedido = withContext(Dispatchers.IO) {
                api.buscarPedidoPorCodigo(codigoPedido)
            }
        } catch (e: Exception) {
            error = "Error al cargar el pedido: ${e.localizedMessage}"
        }
    }

    val fondo = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    pedido?.let { pedido ->
        val (fecha, hora) = formatearFecha(pedido.fechaHora)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fondo)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📦 Detalles del Pedido",
                fontFamily = Oswald,
                fontSize = 32.sp,
                color = Color(0xFF00796B)
            )

            Text("Código: ${pedido.codigoPedido}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Estado: ${pedido.estadoPedido}", fontSize = 18.sp, color = when (pedido.estadoPedido) {
                "EN_PROCESO" -> Color(0xFFFF9800)
                "FINALIZADO" -> Color(0xFF4CAF50)
                "PENDIENTE" -> Color(0xFF2196F3)
                else -> Color.Black
            })
            Text("Mesa: ${pedido.numeroMesa}", fontSize = 18.sp)
            Text("Fecha: $fecha", fontSize = 18.sp)
            Text("Hora: $hora", fontSize = 18.sp)
            Text("Total: €${pedido.total}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Divider(thickness = 1.dp, color = Color.Gray)

            Text("🍽️ Platos", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

            if (pedido.detallesPlatos.isNullOrEmpty()) {
                Text("- No especificado", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            } else {
                pedido.detallesPlatos.forEach { plato ->
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("- ${plato.nombre}", fontSize = 17.sp)
                        Text("  Precio: €${plato.precio}", fontSize = 15.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    } ?: error?.let {
        Text(it, modifier = Modifier.padding(16.dp), color = Color.Red)
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fondo),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
