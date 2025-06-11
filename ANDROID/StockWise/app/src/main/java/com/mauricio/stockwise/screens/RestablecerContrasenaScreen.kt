package com.mauricio.stockwise.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun RestablecerContrasenaScreen(navController: NavController) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📧 Recuperar contraseña",
                fontSize = 24.sp,
                color = Color(0xFF00796B),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    mensaje = null
                    if (correo.isBlank() || !correo.contains("@")) {
                        mensaje = "Introduce un correo válido."
                        return@Button
                    }

                    cargando = true
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val response = RetrofitClient.apiService.solicitarResetPassword(correo)
                            if (response.isSuccessful) {
                                mensaje = "📩 Correo enviado."
                            } else if (response.code() == 404) {
                                mensaje = "⚠️ Correo no encontrado."
                            } else {
                                mensaje = "❌ Error inesperado."
                            }
                        } catch (e: HttpException) {
                            mensaje = "❌ Error del servidor."
                        } catch (e: Exception) {
                            mensaje = "⚠️ Sin conexión o fallo interno."
                        } finally {
                            cargando = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Enviar correo")
            }

            mensaje?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = Color.DarkGray)
            }
        }
    }
}
