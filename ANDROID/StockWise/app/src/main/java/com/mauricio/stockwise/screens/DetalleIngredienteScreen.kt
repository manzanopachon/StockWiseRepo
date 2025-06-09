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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIngredienteScreen(navController: NavController, ingredienteId: Long) {
    val api = RetrofitClient.apiService
    var ingrediente by remember { mutableStateOf<Ingrediente?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(ingredienteId) {
        try {
            val result = withContext(Dispatchers.IO) {
                api.getIngredienteById(ingredienteId)
            }
            ingrediente = result
        } catch (e: Exception) {
            error = "Error al cargar ingrediente: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📃 Detalle del Ingrediente",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        visible = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()

                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
                        )
                    )
                    .padding(24.dp)
                    .hideKeyboardOnTap()
            ) {
                ingrediente?.let { ing ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🧂 ${ing.nombre}",
                            fontFamily = Oswald,
                            fontSize = 35.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF00796B)
                        )
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        @Composable
                        fun labelValue(label: String, value: String) {
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "$label: ",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        labelValue("Nombre", ing.nombre)
                        labelValue("Stock", "${ing.cantidadStock} ${ing.unidadMedida}")
                        labelValue("Proveedor", ing.proveedor.toString())
                        labelValue("Prioridad Alta", ing.prioridadAlta.toString())
                        labelValue("Prioridad Media", ing.prioridadMedia.toString())
                        labelValue("Prioridad Baja", ing.prioridadBaja.toString())

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                navController.navigate("ingredienteEditar/${ing.id}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00796B),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("✏️ Editar")
                        }
                    }
                } ?: run {
                    error?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    } ?: Text("Cargando...", modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Cuando termina la animación, vuelve atrás
        if (!visible) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(300)
                navController.popBackStack()
            }
        }
    }
}
