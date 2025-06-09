package com.mauricio.stockwise.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.model.CategoriaDTO
import com.mauricio.stockwise.model.Ingrediente
import com.mauricio.stockwise.model.IngredienteCantidadDTO
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
fun NuevoPlatoScreen(navController: NavController, restauranteId: Long) {
    val api = RetrofitClient.apiService
    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(true) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var categorias by remember { mutableStateOf<List<CategoriaDTO>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDTO?>(null) }
    var expandedCategoria by remember { mutableStateOf(false) }

    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var ingredientesSeleccionados by remember { mutableStateOf<MutableMap<Long, Double>>(mutableMapOf()) }
    var cantidadesTexto by remember { mutableStateOf<MutableMap<Long, String>>(mutableMapOf()) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(restauranteId) {
        try {
            ingredientes = api.getIngredientesByRestaurante(restauranteId)
            categorias = api.getCategorias()
        } catch (e: Exception) {
            mensajeError = "Error al cargar datos: ${e.localizedMessage}"
        }
    }

    val ingredientesFiltrados = ingredientes.filter {
        it.nombre.contains(searchText, ignoreCase = true) && !ingredientesSeleccionados.containsKey(it.id)
    }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("➕ Nuevo Plato", color = Color.White) },
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
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(gradient)
                    .hideKeyboardOnTap()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (€)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = expandedCategoria, onExpandedChange = { expandedCategoria = !expandedCategoria }) {
                    OutlinedTextField(
                        value = categoriaSeleccionada?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(expanded = expandedCategoria, onDismissRequest = { expandedCategoria = false }) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nombre) },
                                onClick = {
                                    categoriaSeleccionada = cat
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(value = searchText, onValueChange = { searchText = it }, label = { Text("Buscar ingrediente") }, modifier = Modifier.fillMaxWidth())

                if (searchText.isNotBlank()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                        items(ingredientesFiltrados) { ing ->
                            Text(
                                text = ing.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        ingredientesSeleccionados[ing.id] = 0.0
                                        cantidadesTexto[ing.id] = ""
                                        searchText = ""
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                if (ingredientesSeleccionados.isNotEmpty()) {
                    Text("Ingredientes seleccionados:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF00796B))
                    ingredientesSeleccionados.forEach { (id, _) ->
                        val ing = ingredientes.find { it.id == id }
                        val cantidadTexto = cantidadesTexto[id] ?: ""
                        if (ing != null) {
                            OutlinedTextField(
                                value = cantidadTexto,
                                onValueChange = {
                                    cantidadesTexto = cantidadesTexto.toMutableMap().apply { put(id, it) }
                                    ingredientesSeleccionados = ingredientesSeleccionados.toMutableMap().apply {
                                        put(id, it.replace(',', '.').toDoubleOrNull() ?: 0.0)
                                    }
                                },
                                label = { Text("${ing.nombre} (${ing.unidadMedida})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Button(onClick = {
                    if (categoriaSeleccionada == null) {
                        mensajeError = "Selecciona una categoría válida"
                        return@Button
                    }
                    try {
                        val platoDTO = PlatoDTO(
                            id = 0L,
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            categoria = categoriaSeleccionada!!,
                            restauranteId = restauranteId,
                            ingredientes = ingredientesSeleccionados.map { (id, cantidad) ->
                                IngredienteCantidadDTO(ingredienteId = id, cantidad = cantidad)
                            }
                        )

                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) { api.crearPlato(platoDTO) }
                                mensajeExito = "Plato creado correctamente"
                                visible = false
                            } catch (e: Exception) {
                                mensajeError = "Error al crear plato: ${e.localizedMessage}"
                            }
                        }
                    } catch (e: Exception) {
                        mensajeError = "Campos inválidos"
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White)) {
                    Text("Guardar")
                }

                mensajeError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                mensajeExito?.let {
                    Text(it, color = Color(0xFF00796B))
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

