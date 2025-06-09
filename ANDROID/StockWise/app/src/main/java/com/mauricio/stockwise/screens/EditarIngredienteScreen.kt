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
import androidx.compose.material.icons.filled.Save
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
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Oswald
import com.mauricio.stockwise.ui.utils.hideKeyboardOnTap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarIngredienteScreen(navController: NavController, ingredienteId: Long) {
    val api = RetrofitClient.apiService
    var ingrediente by remember { mutableStateOf<Ingrediente?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(ingredienteId) {
        try {
            ingrediente = withContext(Dispatchers.IO) {
                api.getIngredienteById(ingredienteId)
            }
        } catch (e: Exception) {
            error = "Error al cargar: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✏️ Editar Ingrediente", color = Color.White) },
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
                .hideKeyboardOnTap()
        ) {
            ingrediente?.let { ing ->
                var nombre by remember { mutableStateOf(ing.nombre) }
                var stock by remember { mutableStateOf(ing.cantidadStock.toString()) }
                var proveedor by remember { mutableStateOf(ing.proveedor) }
                var unidad by remember { mutableStateOf(ing.unidadMedida) }
                var prioridadAlta by remember { mutableStateOf(ing.prioridadAlta.toString()) }
                var prioridadMedia by remember { mutableStateOf(ing.prioridadMedia.toString()) }
                var prioridadBaja by remember { mutableStateOf(ing.prioridadBaja.toString()) }

                val gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
                    OutlinedTextField(value = proveedor.toString(), onValueChange = { proveedor = it }, label = { Text("Proveedor") })
                    OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad") })
                    OutlinedTextField(value = prioridadAlta, onValueChange = { prioridadAlta = it }, label = { Text("Prioridad Alta") })
                    OutlinedTextField(value = prioridadMedia, onValueChange = { prioridadMedia = it }, label = { Text("Prioridad Media") })
                    OutlinedTextField(value = prioridadBaja, onValueChange = { prioridadBaja = it }, label = { Text("Prioridad Baja") })

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val actualizado = ing.copy(
                                        nombre = nombre,
                                        cantidadStock = stock.toDoubleOrNull() ?: 0.0,
                                        proveedor = proveedor,
                                        unidadMedida = unidad,
                                        prioridadAlta = prioridadAlta.toDoubleOrNull() ?: 0.0,
                                        prioridadMedia = prioridadMedia.toDoubleOrNull() ?: 0.0,
                                        prioridadBaja = prioridadBaja.toDoubleOrNull() ?: 0.0
                                    )

                                    CoroutineScope(Dispatchers.IO).launch {
                                        api.actualizarIngrediente(ingredienteId, actualizado)
                                        withContext(Dispatchers.Main) {
                                            mensajeExito = "Ingrediente actualizado"
                                        }
                                    }
                                } catch (e: Exception) {
                                    error = "Error al actualizar: ${e.localizedMessage}"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Guardar", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar", color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    api.eliminarIngrediente(ingredienteId)
                                    withContext(Dispatchers.Main) {
                                        navController.popBackStack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar", color = Color.White)
                        }
                    }

                    mensajeExito?.let {
                        Text(text = it, color = Color(0xFF00796B))
                    }

                    error?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            } ?: run {
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) } ?: Text("Cargando...")
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
