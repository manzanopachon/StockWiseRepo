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
import com.mauricio.stockwise.model.Ingrediente
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Caveat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DetalleIngredienteScreen(navController: NavController, ingredienteId: Long) {
    val api = RetrofitClient.apiService
    var ingrediente by remember { mutableStateOf<Ingrediente?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
                )
            )
            .padding(24.dp)
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
                    "🧂 Detalle de: ${ing.nombre}",
                    fontFamily = Caveat,
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
                                fontWeight = FontWeight.Bold
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
