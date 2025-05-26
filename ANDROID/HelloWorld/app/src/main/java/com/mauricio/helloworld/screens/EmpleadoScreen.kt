package com.mauricio.helloworld.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.Ingrediente
import com.mauricio.helloworld.RetrofitClient
import com.mauricio.helloworld.ui.theme.Caveat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class OrdenIngrediente {
    NOMBRE, PROVEEDOR, PRIORIDAD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoScreen(navController: NavController, empleadoId: Long, restauranteId: Long) {
    val api = RetrofitClient.apiService
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var ordenSeleccionado by rememberSaveable { mutableStateOf(OrdenIngrediente.NOMBRE) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(empleadoId) {
        try {
            val empleadoResult = withContext(Dispatchers.IO) {
                api.obtenerEmpleadoPorId(empleadoId)
            }
            empleado = empleadoResult

            empleado?.restaurante?.id?.let { id ->
                val ingredientesResult = withContext(Dispatchers.IO) {
                    api.getIngredientesByRestaurante(id)
                }
                ingredientes = ingredientesResult
            } ?: run {
                errorMensaje = "El empleado no tiene restaurante asignado."
            }
        } catch (e: Exception) {
            errorMensaje = "Error de red: ${e.localizedMessage}"
        }
    }

    val ingredientesOrdenados = when (ordenSeleccionado) {
        OrdenIngrediente.NOMBRE -> ingredientes.sortedBy { it.nombre.lowercase() }
        OrdenIngrediente.PROVEEDOR -> ingredientes.sortedBy { it.proveedor?.lowercase() ?: "" }
        OrdenIngrediente.PRIORIDAD -> ingredientes.sortedBy {
            when {
                it.cantidadStock < it.prioridadAlta -> 1
                it.cantidadStock < it.prioridadMedia -> 2
                it.cantidadStock < it.prioridadBaja -> 3
                else -> 4
            }
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
                text = "INGREDIENTES",
                fontFamily = Caveat,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00796B)
            )
            Button(
                onClick = {
                    navController.navigate("nuevoIngrediente/$restauranteId")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)
            ) {
                Text("➕ Nuevo")
            }
        }

        empleado?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👤 Empleado: ${it.nombre}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("📌 ${it.puestoTrabajo}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredientes de \"${empleado?.restaurante?.nombre ?: "?"}\"",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Ordenar")
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Ordenar por nombre") },
                            onClick = {
                                ordenSeleccionado = OrdenIngrediente.NOMBRE
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ordenar por proveedor") },
                            onClick = {
                                ordenSeleccionado = OrdenIngrediente.PROVEEDOR
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ordenar por prioridad") },
                            onClick = {
                                ordenSeleccionado = OrdenIngrediente.PRIORIDAD
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (ingredientesOrdenados.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    ingredientesOrdenados.forEach { ingrediente ->
                        val color = when {
                            ingrediente.cantidadStock < ingrediente.prioridadAlta -> Color(0xFFFFCDD2)
                            ingrediente.cantidadStock < ingrediente.prioridadMedia -> Color(0xFFFFF9C4)
                            ingrediente.cantidadStock < ingrediente.prioridadBaja -> Color(0xFFC8E6C9)
                            else -> Color(0xFFE0E0E0)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("ingredienteDetalle/${ingrediente.id}")
                                },
                            colors = CardDefaults.cardColors(containerColor = color)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = ingrediente.nombre,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                )
                                Text(text = "📦 Stock: ${ingrediente.cantidadStock} ${ingrediente.unidadMedida}")
                                Text(text = "🏷️ Proveedor: ${ingrediente.proveedor ?: "-"}")
                            }
                        }
                    }
                }
            } else {
                Text("No hay ingredientes disponibles.", style = MaterialTheme.typography.bodyMedium)
            }
        } ?: Text("Cargando datos del empleado...", style = MaterialTheme.typography.bodyMedium)

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
