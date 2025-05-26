package com.mauricio.helloworld.screens

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.helloworld.Pedido
import com.mauricio.helloworld.RetrofitClient
import com.mauricio.helloworld.screens.utils.*
import com.mauricio.helloworld.ui.theme.Caveat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PedidosScreen(navController: NavController, restauranteId: Long) {
    val context = LocalContext.current
    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var pedidosFiltrados by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val estadosSeleccionados = remember { mutableStateMapOf<String, String>() }
    var searchQuery by remember { mutableStateOf("") }
    val pedidosFiltradosPorBusqueda = pedidosFiltrados.filter {
        it.codigoPedido.contains(searchQuery, ignoreCase = true)
    }

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
            .background(brush = gradient)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📋 Pedidos del restaurante",
                fontFamily = Caveat,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineSmall, color = Color(0xFF00796B))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
                ) {
                    Text("📅 Filtrar por fecha")
                }

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val total = pedidos.sumOf { it.total }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ganado total: €$total", Toast.LENGTH_LONG).show()
                            }
                        }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("🔍 Buscar código") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pedidosFiltradosPorBusqueda) { pedido ->
                    val estadoSeleccionado = estadosSeleccionados[pedido.codigoPedido] ?: pedido.estadoPedido

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🧾 Código: ${pedido.codigoPedido}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("🪑 Mesa: ${pedido.numeroMesa}", fontWeight = FontWeight.Bold, color = Color(0xFF00796B))

                            Text(
                                "📌 Estado: ${pedido.estadoPedido}",
                                fontWeight = FontWeight.Bold,
                                color = when (pedido.estadoPedido) {
                                    "EN_PROCESO" -> Color(0xFFFF9800)
                                    "FINALIZADO" -> Color(0xFF4CAF50)
                                    "PENDIENTE" -> Color(0xFF2196F3)
                                    else -> Color.Black
                                }
                            )

                            if (estadoSeleccionado == "FINALIZADO" && pedido.estadoPedido != "FINALIZADO") {
                                Text("⚠️ Se descontará stock", color = Color.Red, fontSize = 12.sp)
                            }

                            Text("💰 Total: €${pedido.total}")
                            val (fecha, hora) = formatearFecha(pedido.fechaHora)
                            Text("📅 Fecha: $fecha")
                            Text("⏰ Hora: $hora")

                            Text("🍽️ Platos:", fontWeight = FontWeight.SemiBold)
                            if (pedido.detallesPlatos.isNullOrEmpty()) {
                                Text("- No especificado", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            } else {
                                pedido.detallesPlatos.forEach {
                                    Text("- ${it.nombre}", modifier = Modifier.padding(start = 8.dp))
                                }
                            }

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        actualizarEstado(pedido.codigoPedido, estadoSeleccionado, context) {
                                            Toast.makeText(context, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                                            CoroutineScope(Dispatchers.IO).launch {
                                                recargarPedidos(restauranteId) {
                                                    pedidos = it
                                                    pedidosFiltrados = it
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
                                ) {
                                    Text("✔️ Confirmar")
                                }

                                EstadoDropdown(
                                    pedidoId = pedido.codigoPedido,
                                    estadoActual = estadoSeleccionado,
                                    context = context
                                ) { nuevo ->
                                    estadosSeleccionados[pedido.codigoPedido] = nuevo
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
