package com.mauricio.stockwise.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.model.Ingrediente
import com.mauricio.stockwise.model.PlatoDTO
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
import com.mauricio.stockwise.ui.utils.hideKeyboardOnTap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlatoScreen(
    navController: NavController,
    platoId: Long,
    restauranteId: Long
) {
    val api = RetrofitClient.apiService
    val coroutineScope = rememberCoroutineScope()

    var plato by remember { mutableStateOf<PlatoDTO?>(null) }
    var ingredientesDisponibles by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(platoId) {
        try {
            val platoResult = withContext(Dispatchers.IO) { api.obtenerPlatoPorId(platoId) }
            val ingredientesResult = withContext(Dispatchers.IO) { api.getIngredientesByRestaurante(restauranteId) }
            plato = platoResult
            ingredientesDisponibles = ingredientesResult
        } catch (e: Exception) {
            errorMensaje = "Error al cargar el plato: ${e.localizedMessage}"
        }
    }

    val fondo = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✏️ Editar Plato", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { visible = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00796B))
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        {
            plato?.let { platoActual ->
                var nombre by remember { mutableStateOf(platoActual.nombre) }
                var descripcion by remember { mutableStateOf(platoActual.descripcion) }
                var precio by remember { mutableStateOf(platoActual.precio.toString()) }
                var categoria by remember { mutableStateOf(platoActual.categoria.nombre) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fondo)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") })
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") })
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") })
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") })

                    Text(
                        "Ingredientes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF01579B)
                    )

                    platoActual.ingredientes.forEach { ingredienteCantidad ->
                        val ingrediente =
                            ingredientesDisponibles.find { it.id == ingredienteCantidad.ingredienteId }
                        var cantidadEditada by remember { mutableStateOf(ingredienteCantidad.cantidad.toString()) }

                        ingrediente?.let {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        it.nombre,
                                        color = Color(0xFF00796B),
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedTextField(
                                        value = cantidadEditada,
                                        onValueChange = { cantidadEditada = it },
                                        label = { Text("Cantidad") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    try {
                                                        api.modificarCantidadIngrediente(
                                                            platoActual.id,
                                                            it.id,
                                                            cantidadEditada.toDouble()
                                                        )
                                                        val actualizado =
                                                            api.obtenerPlatoPorId(platoActual.id)
                                                        plato = actualizado
                                                    } catch (e: Exception) {
                                                        errorMensaje =
                                                            "Error al modificar: ${e.localizedMessage}"
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    0xFF0097A7
                                                )
                                            )
                                        ) {
                                            Text("Actualizar")
                                        }

                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    try {
                                                        api.eliminarIngredienteDePlato(
                                                            platoActual.id,
                                                            it.id
                                                        )
                                                        val actualizado =
                                                            api.obtenerPlatoPorId(platoActual.id)
                                                        plato = actualizado
                                                    } catch (e: Exception) {
                                                        errorMensaje =
                                                            "Error al eliminar: ${e.localizedMessage}"
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    0xFFD32F2F
                                                )
                                            )
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    var ingredienteSeleccionadoId by remember { mutableStateOf<Long?>(null) }
                    var nuevaCantidad by remember { mutableStateOf("") }
                    val ingredientesNoAsignados = ingredientesDisponibles.filter { ing ->
                        platoActual.ingredientes.none { it.ingredienteId == ing.id }
                    }

                    Text(
                        "➕ Añadir Ingrediente",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF01579B)
                    )

                    if (ingredientesNoAsignados.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Button(onClick = { expanded = true }) {
                                Text(
                                    ingredientesNoAsignados.find { it.id == ingredienteSeleccionadoId }?.nombre
                                        ?: "Seleccionar ingrediente"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                ingredientesNoAsignados.forEach { ingrediente ->
                                    DropdownMenuItem(
                                        text = { Text(ingrediente.nombre) },
                                        onClick = {
                                            ingredienteSeleccionadoId = ingrediente.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = nuevaCantidad,
                            onValueChange = { nuevaCantidad = it },
                            label = { Text("Cantidad") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (ingredienteSeleccionadoId != null && nuevaCantidad.toDoubleOrNull() != null) {
                                    coroutineScope.launch {
                                        try {
                                            api.asignarIngredientesAlPlato(
                                                platoActual.id,
                                                mapOf(ingredienteSeleccionadoId!! to nuevaCantidad.toDouble())
                                            )
                                            val actualizado = api.obtenerPlatoPorId(platoActual.id)
                                            plato = actualizado
                                            nuevaCantidad = ""
                                            ingredienteSeleccionadoId = null
                                        } catch (e: Exception) {
                                            errorMensaje =
                                                "Error al añadir ingrediente: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                        ) {
                            Text("Añadir")
                        }
                    } else {
                        Text("No hay ingredientes disponibles para añadir.")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    api.actualizarPlato(
                                        platoActual.id, platoActual.copy(
                                            nombre = nombre,
                                            descripcion = descripcion,
                                            precio = precio.toDoubleOrNull() ?: 0.0,
                                            categoria = platoActual.categoria.copy(nombre = categoria)
                                        )
                                    )
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    errorMensaje = "Error al guardar: ${e.localizedMessage}"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Text("Guardar cambios")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    api.eliminarPlato(platoActual.id)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    errorMensaje = "Error al eliminar: ${e.localizedMessage}"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Eliminar plato")
                    }

                    errorMensaje?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fondo)
                        .hideKeyboardOnTap(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        if (!visible) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(300)
                navController.popBackStack()
            }
        }
    }
}
