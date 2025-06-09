package com.mauricio.stockwise.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.model.Pedido
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.screens.utils.*
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
import com.mauricio.stockwise.ui.utils.hideKeyboardOnTap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PedidosScreen(navController: NavController, restauranteId: Long) {
    val context = LocalContext.current
    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var pedidosFiltrados by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val pedidosFiltradosPorBusqueda = pedidosFiltrados
        .filter { it.codigoPedido.contains(searchQuery, ignoreCase = true) }
        .reversed() // Pedidos más recientes primero

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    LaunchedEffect(Unit) {
        try {
            val pedidosApi = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerTodosLosPedidos(restauranteId)
            }
            pedidos = pedidosApi
            pedidosFiltrados = pedidosApi
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .hideKeyboardOnTap()
            .background(brush = gradient)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📋 Pedidos del restaurante",
                fontFamily = Oswald,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF00796B)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
                ) {
                    Text("📅 Filtrar por fecha")
                }

                Button(
                    onClick = {
                        val totalVisible = pedidosFiltradosPorBusqueda.sumOf { it.total }
                        Toast.makeText(context, "Ganado total: €$totalVisible", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
                ) {
                    Text("💰 Total")
                }
            }

            if (fechaSeleccionada.isNotEmpty()) {
                Text("📆 Filtrado por: $fechaSeleccionada", modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("🔍 Buscar código") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pedidosFiltradosPorBusqueda) { pedido ->
                    val estadoSeleccionado = remember { mutableStateOf(pedido.estadoPedido) }
                    val (fecha, hora) = formatearFecha(pedido.fechaHora)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("detallePedido/${pedido.codigoPedido}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🧾 Código: ${pedido.codigoPedido}", fontSize = 18.sp, color = Color(0xFF00796B))
                            Text(
                                "📌 Estado: ${estadoSeleccionado.value}",
                                fontSize = 16.sp,
                                color = when (estadoSeleccionado.value) {
                                    "EN_PROCESO" -> Color(0xFFFF9800)
                                    "FINALIZADO" -> Color(0xFF4CAF50)
                                    "PENDIENTE" -> Color(0xFF2196F3)
                                    else -> Color.Black
                                }
                            )
                            Text("📅 Fecha: $fecha")
                            Text("⏰ Hora: $hora")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        actualizarEstado(
                                            pedido.codigoPedido,
                                            estadoSeleccionado.value,
                                            context
                                        ) {
                                            Toast.makeText(context, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                                            CoroutineScope(Dispatchers.IO).launch {
                                                recargarPedidos(restauranteId) {
                                                    pedidos = it
                                                    pedidosFiltrados = it
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                                ) {
                                    Text("✔️ Confirmar")
                                }

                                EstadoDropdown(
                                    pedidoId = pedido.codigoPedido,
                                    estadoActual = estadoSeleccionado.value,
                                    context = context
                                ) { nuevo ->
                                    estadoSeleccionado.value = nuevo
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                fechaSeleccionada = selectedDate
                pedidosFiltrados = pedidos.filter {
                    it.fechaHora.substring(0, 10) == selectedDate
                }
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
