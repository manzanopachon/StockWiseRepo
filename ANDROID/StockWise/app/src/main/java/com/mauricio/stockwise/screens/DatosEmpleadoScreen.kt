package com.mauricio.stockwise.screens

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
import com.mauricio.stockwise.model.Empleado
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DatosEmpleadoScreen(navController: NavController, empleadoId: Long) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(empleadoId) {
        try {
            val result = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerEmpleadoPorId(empleadoId)
            }
            empleado = result
        } catch (_: Exception) {}
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Text(
                "📋 Datos del empleado",
                fontFamily = Oswald,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF00796B),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            empleado?.let {
                DatoItem(titulo = "👤 Nombre", valor = it.nombre)
                DatoItem(titulo = "🧾 Apellidos", valor = it.apellidos)
                DatoItem(titulo = "📧 Correo", valor = it.correo)
                DatoItem(titulo = "💼 Puesto", valor = it.puestoTrabajo)
                DatoItem(titulo = "🏢 Restaurante", valor = it.restaurante.nombre)
            } ?: Text("Cargando datos...", fontSize = 18.sp)

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión", color = Color.White, fontSize = 16.sp)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "¿Cerrar sesión?",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que quieres cerrar sesión?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00796B),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("No")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            )

        }
    }
}

@Composable
fun DatoItem(titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$titulo: ",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = valor,
            fontSize = 18.sp
        )
    }
}