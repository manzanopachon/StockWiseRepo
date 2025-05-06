package com.mauricio.helloworld

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import android.util.Log

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mauricio.helloworld.RetrofitClient

@Composable
fun HomeScreen(onEmpleadoClick: () -> Unit, onClienteClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(onClick = onClienteClick, modifier = Modifier.fillMaxWidth()) {
            Text("Cliente")
        }
        Button(onClick = onEmpleadoClick, modifier = Modifier.fillMaxWidth()) {
            Text("Empleado")
        }
    }
}

@Composable
fun EmpleadoOptionsScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar Sesión")
        }
        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("Registrarse")
        }
    }
}

@Composable
fun RegistroScreen(onRegisterSuccess: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var restaurante by remember { mutableStateOf("") }
    var puesto by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la página
        Text(
            text = "REGISTRARSE",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Ocupa el espacio restante y permite centrar verticalmente
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre los elementos del formulario
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") })
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") })
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") })

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
                    }
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Repetir Contraseña") },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (isConfirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = restaurante,
                    onValueChange = { restaurante = it },
                    label = { Text("Restaurante") })
                OutlinedTextField(
                    value = puesto,
                    onValueChange = { puesto = it },
                    label = { Text("Puesto de trabajo") })

                Button(onClick = {
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = ""

                    // Paso 1: buscar el restaurante por nombre
                    RetrofitInstance.api.buscarRestaurantePorNombre(restaurante)
                        .enqueue(object : Callback<Restaurante> {
                            override fun onResponse(
                                call: Call<Restaurante>,
                                response: Response<Restaurante>
                            ) {
                                if (response.isSuccessful) {
                                    val restauranteEncontrado = response.body()
                                    if (restauranteEncontrado != null) {
                                        // Paso 2: registrar al empleado
                                        val nuevoEmpleado = Empleado(
                                            id = null, // Se ignora al crear
                                            nombre = nombre,
                                            apellidos = apellidos,
                                            correo = email,
                                            contraseña = password,
                                            puestoTrabajo = puesto,
                                            restaurante = restauranteEncontrado,
                                            codigoValidacion = "",
                                            validado = false
                                        )

                                        RetrofitInstance.api.registrarEmpleado(nuevoEmpleado)
                                            .enqueue(object : Callback<Empleado> {
                                                override fun onResponse(
                                                    call: Call<Empleado>,
                                                    response: Response<Empleado>
                                                ) {
                                                    isLoading = false
                                                    if (response.isSuccessful) {
                                                        onRegisterSuccess()
                                                    } else {
                                                        errorMessage = "Error al registrar empleado"
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Empleado>,
                                                    t: Throwable
                                                ) {
                                                    isLoading = false
                                                    errorMessage = "Error de red: ${t.message}"
                                                }
                                            })
                                    } else {
                                        isLoading = false
                                        errorMessage = "Restaurante no encontrado"
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "Elige un restaurante correcto"
                                }
                            }

                            override fun onFailure(call: Call<Restaurante>, t: Throwable) {
                                isLoading = false
                                errorMessage = "Error de red: ${t.message}"
                            }
                        })
                }) {
                    Text("Registrarse")
                }


                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

    @Composable
    fun VerificacionCodigoScreen(navController: NavController, empleadoId: Long) {
        var codigoIngresado by remember { mutableStateOf("") }
        var intentosRestantes by remember { mutableStateOf(3) }
        var mensajeError by remember { mutableStateOf("") }
        val api = RetrofitClient.apiService

        // Función para verificar el código ingresado
        fun verificarCodigo() {
            // Llamada al API para verificar el código
            api.verificarCodigo(empleadoId, codigoIngresado).enqueue(object : Callback<Empleado> {
                override fun onResponse(call: Call<Empleado>, response: Response<Empleado>) {
                    val empleado = response.body()
                    if (response.isSuccessful && empleado != null) {
                        // Si el código es correcto, marcamos al empleado como validado
                        if (codigoIngresado == empleado.codigoValidacion) {
                            // Validación exitosa → redirigir a pantalla principal
                            api.marcarEmpleadoComoValidado(empleadoId)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        // Resetear intentos restantes a 3
                                        intentosRestantes = 3
                                        navController.navigate("main")
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        mensajeError = "Error al actualizar validación"
                                    }
                                })
                        } else {
                            intentosRestantes -= 1
                            if (intentosRestantes <= 0) {
                                // Regenerar código
                                api.regenerarCodigo(empleadoId).enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        mensajeError =
                                            "Se ha generado un nuevo código. Inténtalo de nuevo."
                                        intentosRestantes = 3 // Resetear intentos restantes
                                        codigoIngresado = "" // Reseteamos el código ingresado
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        mensajeError = "Error al regenerar el código"
                                    }
                                })
                            } else {
                                mensajeError =
                                    "Código incorrecto. Intentos restantes: $intentosRestantes"
                            }
                        }

                    } else {
                        mensajeError = "Error al obtener el empleado. Intenta de nuevo más tarde."
                    }
                }

                override fun onFailure(call: Call<Empleado>, t: Throwable) {
                    mensajeError = "Error de red: ${t.message}. Verifica tu conexión."
                }
            })
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Introduce el código de validación")

            // Campo de texto para ingresar el código
            OutlinedTextField(
                value = codigoIngresado,
                onValueChange = { codigoIngresado = it },
                label = { Text("Código (5 dígitos)") }
            )

            // Botón para verificar el código ingresado
            Button(onClick = { verificarCodigo() }) {
                Text("Verificar")
            }

            // Mostrar mensaje de error si existe
            if (mensajeError.isNotEmpty()) {
                Text(mensajeError, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    @Composable
    fun LoginScreen(navController: NavController) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        // Nuevo estado para controlar la visibilidad de la contraseña
        var isPasswordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la página
            Text(
                text = "INICIAR SESIÓN",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa el espacio restante y permite centrar verticalmente
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") })
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    // Utiliza PasswordVisualTransformation si la contraseña no está visible
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )

                Button(onClick = {
                    val request = LoginRequest(email, password)
                    val api = RetrofitClient.apiService // tu instancia de retrofit

                    api.loginEmpleado(request).enqueue(object :
                        Callback<LoginResponse> {  // Cambié Map<String, Any> por LoginResponse
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    val requiereCodigo = loginResponse.requiereCodigo
                                    val mensaje = loginResponse.mensaje
                                    val empleadoId = loginResponse.empleadoId
                                    val nombreEmpleado = loginResponse.nombreEmpleado
                                        ?: "Desconocido"  // Asegúrate de obtener el nombre

                                    if (requiereCodigo) {
                                        // Si el código es necesario, navega a la pantalla de verificación
                                        navController.navigate("verificarCodigo/$empleadoId")
                                    } else {
                                        // Navegar a la pantalla de bienvenida con el nombre y empleadoId
                                        navController.navigate("bienvenida/$nombreEmpleado/$empleadoId")
                                    }
                                } else {
                                    errorMessage = "Empleado no encontrado."
                                }
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            errorMessage = "Error de red: ${t.message}"
                        }
                    })
                }) {
                    Text("Iniciar sesión")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }

    }

    @Composable
    fun VerificarCodigoDialog(
        empleadoId: Long,
        onCodigoCorrecto: () -> Unit,
        onCancel: () -> Unit
    ) {
        var codigoIngresado by remember { mutableStateOf("") }
        var intentosRestantes by remember { mutableStateOf(3) }
        var errorMensaje by remember { mutableStateOf("") }
        var isDialogOpen by remember { mutableStateOf(true) }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { onCancel() },
                title = { Text("Verificación requerida") },
                text = {
                    Column {
                        Text("Introduce el código de validación")
                        OutlinedTextField(
                            value = codigoIngresado,
                            onValueChange = { codigoIngresado = it },
                            label = { Text("Código (5 dígitos)") }
                        )
                        if (errorMensaje.isNotEmpty()) {
                            Text(text = errorMensaje, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val api = RetrofitClient.apiService
                        api.verificarCodigo(empleadoId, codigoIngresado)
                            .enqueue(object : Callback<Empleado> {
                                override fun onResponse(
                                    call: Call<Empleado>,
                                    response: Response<Empleado>
                                ) {
                                    if (response.isSuccessful) {
                                        // Verificamos el estado actualizado del empleado desde el backend
                                        api.obtenerEmpleadoPorId(empleadoId)
                                            .enqueue(object : Callback<Empleado> {
                                                override fun onResponse(
                                                    call: Call<Empleado>,
                                                    response: Response<Empleado>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        val empleadoActualizado = response.body()
                                                        if (empleadoActualizado != null && empleadoActualizado.validado) {
                                                            isDialogOpen = false
                                                            onCodigoCorrecto()
                                                        } else {
                                                            intentosRestantes--
                                                            if (intentosRestantes == 0) {
                                                                errorMensaje =
                                                                    "Código incorrecto. Se ha generado uno nuevo. Revisa tu correo."
                                                                intentosRestantes = 3
                                                            } else {
                                                                errorMensaje =
                                                                    "Código incorrecto. Intentos restantes: $intentosRestantes"
                                                            }
                                                        }
                                                    } else {
                                                        errorMensaje =
                                                            "Error al obtener estado actualizado del empleado"
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Empleado>,
                                                    t: Throwable
                                                ) {
                                                    errorMensaje = "Error de red: ${t.message}"
                                                }
                                            })
                                    } else {
                                        errorMensaje = "Error al verificar el código"
                                    }
                                }

                                override fun onFailure(call: Call<Empleado>, t: Throwable) {
                                    errorMensaje = "Error de red: ${t.message}"
                                }
                            })
                    }) {
                        Text("Verificar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isDialogOpen = false
                        onCancel()
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }

@Composable
fun PantallaBienvenida(navController: NavController, nombreEmpleado: String, empleadoId: Long) {
    var restauranteId by remember { mutableStateOf<Long?>(null) }
    val api = RetrofitClient.apiService

    LaunchedEffect(empleadoId) {
        api.obtenerEmpleadoPorId(empleadoId).enqueue(object : Callback<Empleado> {
            override fun onResponse(call: Call<Empleado>, response: Response<Empleado>) {
                if (response.isSuccessful) {
                    val empleado = response.body()
                    restauranteId = empleado?.restaurante?.id
                }
            }

            override fun onFailure(call: Call<Empleado>, t: Throwable) {
                // Manejar error
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centra verticalmente
    ) {
        Text(
            "Bienvenido, $nombreEmpleado",
            style = MaterialTheme.typography.headlineLarge, // Un estilo de bienvenida más destacado
            modifier = Modifier.padding(bottom = 24.dp) // Más espacio antes del botón
        )
        Button(
            onClick = {
                restauranteId?.let {
                    navController.navigate("ingredientes/$empleadoId/$it")
                }
            },
            modifier = Modifier.padding(top = 16.dp) // Espacio adicional alrededor del botón
        ) {
            Text("Ir a ingredientes", style = MaterialTheme.typography.bodyMedium) // Estilo al texto del botón
        }
    }
}

@Composable
fun DatosEmpleadoScreen(navController: NavController, empleadoId: Long) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    val api = RetrofitClient.apiService

    LaunchedEffect(empleadoId) {
        api.obtenerEmpleadoPorId(empleadoId).enqueue(object : Callback<Empleado> {
            override fun onResponse(call: Call<Empleado>, response: Response<Empleado>) {
                if (response.isSuccessful) {
                    empleado = response.body()
                }
            }

            override fun onFailure(call: Call<Empleado>, t: Throwable) {
                // Manejo de error si no se puede obtener los datos del empleado
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centra verticalmente
    ) {
        Text(
            text = "Datos del empleado",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        empleado?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre los datos
            ) {
                Text(text = "Nombre: ${it.nombre}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Correo: ${it.correo}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Posición: ${it.puestoTrabajo}", style = MaterialTheme.typography.bodyLarge)
                // Agregar más campos según sea necesario con estilo
            }
        } ?: Text("Cargando datos...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun EmpleadoScreen(navController: NavController, empleadoId: Long, restauranteId: Long) {
    val api = RetrofitClient.apiService
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val response = api.obtenerEmpleadoPorId(empleadoId).execute()
            if (response.isSuccessful) {
                empleado = response.body()
                empleado?.restaurante?.id?.let { restauranteId ->
                    ingredientes = api.getIngredientesByRestaurante(restauranteId)
                } ?: run {
                    errorMensaje = "El empleado no tiene restaurante asignado."
                }
            } else {
                errorMensaje = "Error al obtener datos del empleado"
            }
        } catch (e: Exception) {
            errorMensaje = "Error de red: ${e.localizedMessage}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre secciones
    ) {
        Text("Pantalla del Empleado", style = MaterialTheme.typography.headlineMedium)

        empleado?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Empleado: ${it.nombre}", style = MaterialTheme.typography.bodyLarge)
                Text("(${it.puestoTrabajo})", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))

            // Mostrar ingredientes
            Text("Ingredientes del restaurante:", style = MaterialTheme.typography.titleLarge)
            if (ingredientes.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.Start) {
                    ingredientes.forEach { ingrediente ->
                        Text("- ${ingrediente.nombre}: ${ingrediente.cantidadStock} ${ingrediente.unidadMedida}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                Text("No hay ingredientes disponibles.", style = MaterialTheme.typography.bodyMedium)
            }
        } ?: Text("Cargando datos del empleado...", style = MaterialTheme.typography.bodyMedium)

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}