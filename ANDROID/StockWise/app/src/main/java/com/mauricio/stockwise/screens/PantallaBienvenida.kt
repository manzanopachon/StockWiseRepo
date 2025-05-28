package com.mauricio.stockwise.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.ui.theme.Caveat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaBienvenida(navController: NavController, nombreEmpleado: String, empleadoId: Long) {
    var restauranteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val empleado = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerEmpleadoPorId(empleadoId)
            }
            restauranteId = empleado.restaurante.id
        } catch (e: Exception) {
            // TODO: Manejar error
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Bienvenido, $nombreEmpleado",
                fontFamily = Caveat,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B),
                modifier = Modifier.padding(bottom = 36.dp)
            )

            val buttonModifier = Modifier
                .width(260.dp)
                .height(64.dp)
                .align(Alignment.CenterHorizontally)

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00796B),
                contentColor = Color.White
            )

            restauranteId?.let { idRest ->
                Button(
                    onClick = { navController.navigate("ingredientes/$empleadoId/$idRest") },
                    colors = buttonColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = buttonModifier
                ) {
                    Text("🥦 Ir a Ingredientes", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("platoScreen/$empleadoId/$idRest") },
                    colors = buttonColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = buttonModifier
                ) {
                    Text("🍕 Ver Platos", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("verPedidos/$empleadoId/$idRest") },
                    colors = buttonColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = buttonModifier
                ) {
                    Text("📋 Ver Pedidos", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = { navController.navigate("empleadoDatos/$empleadoId") },
                colors = buttonColors,
                shape = RoundedCornerShape(16.dp),
                modifier = buttonModifier
            ) {
                Text("👤 Ver mis datos", fontSize = 20.sp)
            }
        }
    }
}


