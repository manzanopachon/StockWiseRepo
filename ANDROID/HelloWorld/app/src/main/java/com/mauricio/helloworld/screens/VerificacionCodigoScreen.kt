package com.mauricio.helloworld.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.RetrofitClient
import com.mauricio.helloworld.ui.theme.Caveat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun VerificacionCodigoScreen(navController: NavController, empleadoId: Long, nombreEmpleado: String) {
    var codigoIngresado by remember { mutableStateOf("") }
    var intentosRestantes by remember { mutableStateOf(3) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val api = RetrofitClient.apiService

    if (mostrarToast && mensajeError.isNotBlank()) {
        Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
        mostrarToast = false
    }

    fun verificarCodigo() {
        if (codigoIngresado.length != 5) {
            mensajeError = "El código debe tener 5 caracteres."
            mostrarToast = true
            return
        }

        api.verificarCodigo(empleadoId, codigoIngresado)
            .enqueue(object : Callback<Empleado> {
                override fun onResponse(call: Call<Empleado>, response: Response<Empleado>) {
                    if (response.isSuccessful) {
                        intentosRestantes = 3
                        mensajeError = ""
                        val nombreCodificado = URLEncoder.encode(nombreEmpleado, StandardCharsets.UTF_8.toString())
                        navController.navigate("login") {
                            popUpTo("verificarCodigo") { inclusive = true }
                        }
                    } else if (response.code() == 403) {
                        intentosRestantes--
                        if (intentosRestantes <= 0) {
                            mensajeError = "Se ha generado un nuevo código. Inténtalo de nuevo."
                            intentosRestantes = 3
                            codigoIngresado = ""
                        } else {
                            mensajeError = "Código incorrecto. Intentos restantes: $intentosRestantes"
                        }
                        mostrarToast = true
                    } else {
                        mensajeError = "Error inesperado: ${response.code()}"
                        mostrarToast = true
                    }
                }

                override fun onFailure(call: Call<Empleado>, t: Throwable) {
                    mensajeError = "Fallo de red: ${t.localizedMessage}"
                    mostrarToast = true
                }
            })
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "🔐 Verificación de Código",
                fontFamily = Caveat,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00796B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = codigoIngresado,
                onValueChange = { codigoIngresado = it },
                label = { Text("Código (5 caracteres)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { verificarCodigo() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Verificar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mensajeError.isNotBlank()) {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

