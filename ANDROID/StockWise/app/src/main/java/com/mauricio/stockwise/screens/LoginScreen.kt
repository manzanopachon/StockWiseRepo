package com.mauricio.stockwise.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.stockwise.model.LoginRequest
import com.mauricio.stockwise.model.LoginResponse
import com.mauricio.stockwise.retrofit.RetrofitClient
import com.mauricio.stockwise.service.LlamadaPollingService
import com.mauricio.stockwise.ui.theme.Oswald
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.platform.LocalFocusManager



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .pointerInteropFilter {
                focusManager.clearFocus()
                false
            },

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
                text = "🔑INICIO DE SESIÓN",
                fontFamily = Oswald,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00796B),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val request = LoginRequest(email, password)
                    val api = RetrofitClient.apiService

                    api.loginEmpleado(request).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    val nombreEmpleado = loginResponse.nombreEmpleado ?: "Empleado"
                                    val empleadoId = loginResponse.empleadoId
                                    val restauranteId = loginResponse.restauranteId
                                    val requiereCodigo = loginResponse.requiereCodigo

                                    val nombreCodificado = URLEncoder.encode(nombreEmpleado, StandardCharsets.UTF_8.toString())

                                    if (!requiereCodigo) {
                                        // 🔔 Pedir permiso de notificaciones (solo Android 13+)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            ActivityCompat.requestPermissions(
                                                context as Activity,
                                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                                1
                                            )
                                        }

                                        // 🚀 Iniciar el servicio de polling de llamadas
                                        val intent = Intent(context, LlamadaPollingService::class.java)
                                        context.startService(intent)

                                        navController.navigate("pantallaPrincipal/$empleadoId/$restauranteId")
                                    } else {
                                        navController.navigate("verificarCodigo/$empleadoId/$nombreCodificado/$restauranteId")
                                    }
                                } else {
                                    errorMessage = "Empleado no encontrado."
                                }
                            } else {
                                errorMessage = "Credenciales incorrectas."
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            errorMessage = "Servido apagado. Intentelo de nuevo"
                        }
                    })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(260.dp)
                    .height(72.dp)
                    .padding(top = 32.dp)
            ) {
                Text(
                    text = "🔓 Iniciar sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
