package com.mauricio.stockwise.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.mauricio.stockwise.model.Ingrediente
import com.mauricio.stockwise.model.PlatoDTO
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
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
    var filtro by remember { mutableStateOf("") }

    LaunchedEffect(empleadoId) {
        try {
            val platosResult = withContext(Dispatchers.IO) {
                api.obtenerPlatosPorRestaurante(restauranteId)

            }
            platos = platosResult
            Log.d("PlatoScreen", "empleadoId=$empleadoId, restauranteId=$restauranteId")

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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                fontFamily = Oswald,
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
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo")
            }
        }

        OutlinedTextField(
            value = filtro,
            onValueChange = { filtro = it },
            label = { Text("🔎Buscar plato...") },
            modifier = Modifier.fillMaxWidth()
        )

        val platosFiltrados = platos.filter {
            it.nombre.contains(filtro, ignoreCase = true)
        }

        val platosPorCategoria = platosFiltrados.groupBy { it.categoria.nombre }

        if (platosFiltrados.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                platosPorCategoria.forEach { (categoria, lista) ->
                    Text(
                        text = categoria,
                        fontSize = 20.sp,
                        color = Color(0xFF00796B)
                    )
                    lista.forEach { plato ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Log.d("PlatoClick", "Navegando a editarPlato/${plato.id}/${plato.restauranteId}")
                                    navController.navigate("editarPlato/${plato.id}/${plato.restauranteId}")
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(plato.nombre, style = MaterialTheme.typography.titleLarge, color = Color(0xFF00796B))

                                plato.ingredientes.takeIf { it.isNotEmpty() }?.forEach { ingredienteCantidad ->
                                    val ingrediente = ingredientes.find { it.id == ingredienteCantidad.ingredienteId }
                                    ingrediente?.let {
                                        Column(modifier = Modifier.padding(top = 4.dp)) {
                                            Text(it.nombre, style = MaterialTheme.typography.bodyMedium)
                                            Text("Cantidad: ${ingredienteCantidad.cantidad}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text("No hay platos que coincidan con la búsqueda.", color = Color.Gray)
        }

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
