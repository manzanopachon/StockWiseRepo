package com.mauricio.helloworld.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.helloworld.Ingrediente
import com.mauricio.helloworld.PlatoDTO
import com.mauricio.helloworld.RetrofitClient
import com.mauricio.helloworld.ui.theme.Caveat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatoScreen(navController: NavController, empleadoId: Long, restauranteId: Long) {
    if (empleadoId == 0L || restauranteId == 0L) {
        Text("Error: datos de empleado o restaurante no válidos", color = MaterialTheme.colorScheme.error)
        return
    }

    val api = RetrofitClient.apiService
    val coroutineScope = rememberCoroutineScope()
    var platos by remember { mutableStateOf<List<PlatoDTO>>(emptyList()) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val platosResult = withContext(Dispatchers.IO) {
                api.obtenerPlatosPorRestaurante(restauranteId)
            }
            platos = platosResult
            val ingredientesResult = withContext(Dispatchers.IO) {
                api.getIngredientesByRestaurante(restauranteId)
            }
            ingredientes = ingredientesResult
        } catch (e: Exception) {
            errorMensaje = "Error al cargar platos: ${e.localizedMessage}"
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLATOS",
                fontFamily = Caveat,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00796B)
            )
            Button(
                onClick = {
                    navController.navigate("nuevoPlato/$restauranteId")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo")
            }
        }

        if (platos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                platos.forEach { plato ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(plato.nombre, style = MaterialTheme.typography.titleLarge, color = Color(0xFF00796B))
                            Text("Precio: ${plato.precio} €")
                            Text("Categoría: ${plato.categoria.nombre}")
                            Text("Descripción: ${plato.descripcion}")

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                api.eliminarPlato(plato.id)
                                                platos = platos.filterNot { it.id == plato.id }
                                            } catch (e: Exception) {
                                                errorMensaje = "Error al eliminar el plato: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
                                ) {
                                    Text("Eliminar")
                                }
                            }

                            plato.ingredientes.takeIf { it.isNotEmpty() }?.forEach { ingredienteCantidad ->
                                val ingrediente = ingredientes.find { it.id == ingredienteCantidad.ingredienteId }
                                ingrediente?.let {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(it.nombre, style = MaterialTheme.typography.titleMedium)
                                            Text("Cantidad: ${ingredienteCantidad.cantidad}")
                                        }
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    try {
                                                        api.eliminarIngredienteDePlato(plato.id, it.id)
                                                        platos = platos.map { p ->
                                                            if (p.id == plato.id) {
                                                                p.copy(
                                                                    ingredientes = p.ingredientes.filterNot { ing -> ing.ingredienteId == it.id }
                                                                )
                                                            } else p
                                                        }
                                                    } catch (e: Exception) {
                                                        errorMensaje = "Error al eliminar el ingrediente: ${e.localizedMessage}"
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
                                        ) {
                                            Text("Eliminar Ingrediente")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text("No hay platos registrados.", color = Color.DarkGray)
        }

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
