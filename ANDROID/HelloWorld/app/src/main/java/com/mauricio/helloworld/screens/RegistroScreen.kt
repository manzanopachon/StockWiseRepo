package com.mauricio.helloworld.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.Restaurante
import com.mauricio.helloworld.RetrofitInstance
import com.mauricio.helloworld.ui.theme.Caveat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var puesto by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var restaurantes by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var selectedRestaurante by remember { mutableStateOf<Restaurante?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.obtenerTodosLosRestaurantes().execute()
            }
            if (response.isSuccessful) {
                restaurantes = response.body() ?: emptyList()
            } else {
                errorMessage = "Error en la respuesta del servidor"
            }
        } catch (e: Exception) {
            errorMessage = "Error cargando restaurantes: ${e.localizedMessage}"
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
                "REGISTRO DE EMPLEADO",
                fontFamily = Caveat,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00796B)
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Repetir Contraseña") },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedRestaurante?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona Restaurante") },
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    restaurantes.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) },
                            onClick = {
                                selectedRestaurante = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = puesto, onValueChange = { puesto = it }, label = { Text("Puesto de trabajo") })

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedRestaurante == null) {
                        errorMessage = "Debes seleccionar un restaurante"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }

                    coroutineScope.launch {
                        isLoading = true
                        try {
                            val empleado = Empleado(
                                id = null,
                                nombre = nombre,
                                apellidos = apellidos,
                                correo = email,
                                contraseña = password,
                                puestoTrabajo = puesto,
                                restaurante = selectedRestaurante!!,
                                codigoValidacion = "",
                                validado = false
                            )
                            val response = withContext(Dispatchers.IO) {
                                RetrofitInstance.api.registrarEmpleado(empleado).execute()
                            }
                            isLoading = false
                            if (response.isSuccessful) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = "Error al registrar empleado"
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = "Error de red: ${e.localizedMessage}"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Registrarse")
            }

            if (isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(color = Color(0xFF00796B))
            }
        }
    }
}
