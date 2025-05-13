package com.mauricio.helloworld

import android.net.Uri
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mauricio.helloworld.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import retrofit2.HttpException

import java.io.File


@Composable
fun HomeScreen(onEmpleadoClick: () -> Unit, onClienteClick: () -> Unit) {

    Text(
        text = "PÁGINA PRINCIPAL",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary, // para usar un color temático
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 24.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(onClick = onClienteClick, modifier = Modifier.fillMaxWidth()) {
            Text("Cliente")
        }
        Button(onClick = onEmpleadoClick, modifier = Modifier.fillMaxWidth()) {
            Text("Empleado")
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QrScanner(
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var alreadyScanned by remember { mutableStateOf(false) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner = BarcodeScanning.getClient()

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null && !alreadyScanned) {
                val inputImage = InputImage.fromMediaImage(
                    mediaImage, imageProxy.imageInfo.rotationDegrees
                )

                barcodeScanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue
                            if (!rawValue.isNullOrBlank()) {
                                alreadyScanned = true
                                onQrCodeScanned(rawValue)
                            }
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun ClienteScreen(
    onContinuarClick: (Int, Int) -> Unit
) {
    var qrContent by remember { mutableStateOf<String?>(null) }
    var numeroMesa by remember { mutableStateOf("") }
    var restauranteId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Escanea el código QR del restaurante")

        if (qrContent == null) {
            QrScanner { content ->
                qrContent = content
                val uri = Uri.parse(content)
                restauranteId = uri.getQueryParameter("restauranteId")?.toIntOrNull()
                numeroMesa = uri.getQueryParameter("id_mesa") ?: ""
            }
        } else {
            Text("Código QR leído:")
            Text("Restaurante ID: $restauranteId")
            Text("Mesa: $numeroMesa")

            Button(
                onClick = {
                    val idMesa = numeroMesa.toIntOrNull() ?: 0
                    restauranteId?.let { idRestaurante ->
                        onContinuarClick(idRestaurante, idMesa)
                    }
                },
                enabled = restauranteId != null && numeroMesa.isNotBlank(),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Ver Carta")
            }
        }
    }
}


@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun EmpleadoOptionsScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {

    Text(
        text = "EMPLEADO",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary, // para usar un color temático
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 24.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

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
            color = MaterialTheme.colorScheme.primary, // para usar un color temático
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
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
                    val errores = mutableListOf<String>()

                    // Validación del correo
                    if (!email.contains("@") || !email.contains(".")) {
                        errores.add("El correo debe contener '@' y al menos un '.'")
                    }

                    // Validación de la contraseña
                    if (password.length <= 8) errores.add("La contraseña debe tener más de 8 caracteres")
                    if (password.count { it.isDigit() } < 2) errores.add("La contraseña debe tener al menos 2 números")
                    if (!password.any { it.isUpperCase() }) errores.add("La contraseña debe tener al menos una letra mayúscula")

                    if (password != confirmPassword) {
                        errores.add("Las contraseñas no coinciden")
                    }

                    if (errores.isNotEmpty()) {
                        errorMessage = errores.joinToString("\n")
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
fun VerificacionCodigoScreen(navController: NavController, empleadoId: Long, nombreEmpleado: String){
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
                        // Código correcto
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Introduce el código de validación", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigoIngresado,
            onValueChange = { codigoIngresado = it },
            label = { Text("Código (5 caracteres)") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { verificarCodigo() }) {
            Text("Verificar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeError.isNotBlank()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
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
                text = "INICIO SESIÓN",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary, // para usar un color temático
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
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
                                    val restauranteId = loginResponse.restauranteId
                                    if (requiereCodigo) {
                                        // Si el código es necesario, navega a la pantalla de verificación
                                        val nombreCodificado = URLEncoder.encode(nombreEmpleado, StandardCharsets.UTF_8.toString())
                                        navController.navigate("verificarCodigo/$empleadoId/$nombreCodificado/$restauranteId")

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
fun PantallaBienvenida(navController: NavController, nombreEmpleado: String, empleadoId: Long) {
    var restauranteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val empleado = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerEmpleadoPorId(empleadoId)
            }
            restauranteId = empleado.restaurante.id
        } catch (e: Exception) {
            // Error al obtener el restaurante
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Bienvenido, $nombreEmpleado",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        restauranteId?.let { idRest ->
            Button(
                onClick = {
                    navController.navigate("ingredientes/$empleadoId/$idRest")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Ir a ingredientes")
            }

            Button(
                onClick = {
                    navController.navigate("platoScreen/$empleadoId/$idRest")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Ver Platos")
            }
        }

        Button(
            onClick = {
                navController.navigate("empleadoDatos/$empleadoId")
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ver mis datos")
        }

    }
}


@Composable
fun DatosEmpleadoScreen(navController: NavController, empleadoId: Long) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val result = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerEmpleadoPorId(empleadoId)
            }
            empleado = result
        } catch (e: Exception) {
            // Manejo del error
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Datos del empleado",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        empleado?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Nombre: ${it.nombre}")
                Text("Apellidos: ${it.apellidos}")
                Text("Correo: ${it.correo}")
                Text("Posición: ${it.puestoTrabajo}")
                Text("Restaurante: ${it.restaurante.nombre}")
            }
        } ?: Text("Cargando datos...")

        Spacer(modifier = Modifier.height(32.dp)) // Espacio entre los datos y el botón de cerrar sesión

        // Botón para cerrar sesión
        Button(
            onClick = {
                // Aquí puedes añadir la lógica para limpiar los datos de sesión, como eliminar tokens
                // Ejemplo: SessionManager.logout()

                // Regresar a la pantalla de inicio
                navController.navigate("home") {
                    // Si quieres asegurarte de que la pantalla "home" es la única visible
                    // y eliminar todas las pantallas anteriores en la pila de navegación
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Text("Cerrar sesión")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoScreen(navController: NavController, empleadoId: Long, restauranteId: Long) {
    val api = RetrofitClient.apiService
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val empleadoResult = withContext(Dispatchers.IO) {
                api.obtenerEmpleadoPorId(empleadoId)
            }
            empleado = empleadoResult

            empleado?.restaurante?.id?.let { id ->
                val ingredientesResult = withContext(Dispatchers.IO) {
                    api.getIngredientesByRestaurante(id)
                }
                ingredientes = ingredientesResult
            } ?: run {
                errorMensaje = "El empleado no tiene restaurante asignado."
            }
        } catch (e: Exception) {
            errorMensaje = "Error de red: ${e.localizedMessage}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado con título y botón
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INGREDIENTES",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = {
                    navController.navigate("nuevoIngrediente/$restauranteId")
                }
            ) {
                Text("Nuevo")
            }
        }

        empleado?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Empleado: ${it.nombre}", style = MaterialTheme.typography.bodyLarge)
                Text("(${it.puestoTrabajo})", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))

            Text("Ingredientes del restaurante:", style = MaterialTheme.typography.titleLarge)
            if (ingredientes.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.Start) {
                    ingredientes.forEach { ingrediente ->
                        Text("- ${ingrediente.nombre}: ${ingrediente.cantidadStock} ${ingrediente.unidadMedida}")
                    }
                }
            } else {
                Text("No hay ingredientes disponibles.")
            }
        } ?: Text("Cargando datos del empleado...")

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }

}


@Composable
fun IngredienteScreen(navController: NavController, restauranteId: Long) {
    val context = LocalContext.current
    val api = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var prioridadBaja by remember { mutableStateOf("") }
    var prioridadMedia by remember { mutableStateOf("") }
    var prioridadAlta by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    val fotoUrl = remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val filename = "foto_ingrediente_${System.currentTimeMillis()}.jpg"
            try {
                val stream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.close()
                fotoUrl.value = File(context.filesDir, filename).absolutePath
                Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Añadir Ingrediente",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary, // para usar un color temático
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
            )

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad de medida") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = prioridadBaja, onValueChange = { prioridadBaja = it }, label = { Text("Prioridad baja") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = prioridadMedia, onValueChange = { prioridadMedia = it }, label = { Text("Prioridad media") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = prioridadAlta, onValueChange = { prioridadAlta = it }, label = { Text("Prioridad alta") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = proveedor, onValueChange = { proveedor = it }, label = { Text("Proveedor") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tomar Foto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                try {
                    val ingrediente = IngredienteRequest(
                        nombre = nombre,
                        cantidadStock = cantidad.toDouble(),
                        unidadMedida = unidad,
                        prioridadBaja = prioridadBaja.toDouble(),
                        prioridadMedia = prioridadMedia.toDouble(),
                        prioridadAlta = prioridadAlta.toDouble(),
                        proveedor = proveedor,
                        fotoUrl = fotoUrl.value,
                        restauranteId = restauranteId
                    )

                    api.crearIngrediente(ingrediente).enqueue(object : Callback<IngredienteResponse> {
                        override fun onResponse(call: Call<IngredienteResponse>, response: Response<IngredienteResponse>) {
                            if (response.isSuccessful) {
                                val ingredienteCreado = response.body()
                                Toast.makeText(context, "Ingrediente creado: ${ingredienteCreado?.nombre}", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Error al crear: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<IngredienteResponse>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                } catch (e: Exception) {
                    Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Ingrediente")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatoScreen(navController: NavController, empleadoId: Long, restauranteId: Long) {
    if (empleadoId == 0L || restauranteId == 0L) {
        // Mostrar mensaje de error o redirigir a otra pantalla
        Text("Error: datos de empleado o restaurante no válidos", color = MaterialTheme.colorScheme.error)
        return
    }

    val api = RetrofitClient.apiService
    val coroutineScope = rememberCoroutineScope()
    var platos by remember { mutableStateOf<List<PlatoDTO>>(emptyList()) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Obtener los platos e ingredientes
    LaunchedEffect(empleadoId) {
        try {
            val platosResult = withContext(Dispatchers.IO) {
                api.obtenerPlatosPorRestaurante(restauranteId)
            }
            platos = platosResult
            Log.d("Platos", "Platos obtenidos: $platosResult") // Agrega un log aquí para inspeccionar los datos
            val ingredientesResult = withContext(Dispatchers.IO) {
                api.getIngredientesByRestaurante(restauranteId)
            }
            ingredientes = ingredientesResult
        } catch (e: Exception) {
            errorMensaje = "Error al cargar platos: ${e.localizedMessage}"
            Log.e("ErrorPlatos", e.localizedMessage, e) // Log para capturar el error
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLATOS",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = {
                navController.navigate("nuevoPlato/$restauranteId")
            }) {
                Text("Nuevo")
            }
        }

        if (platos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                platos.forEach { plato ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(plato.nombre, style = MaterialTheme.typography.titleLarge)
                            Text("Precio: ${plato.precio} €")
                            Text("Categoría: ${plato.categoria.nombre}")
                            Text("Descripción: ${plato.descripcion}")

                            // Botón para eliminar plato
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                api.eliminarPlato(plato.id)
                                                platos = platos.filterNot { it.id == plato.id }
                                            } catch (e: Exception) {
                                                errorMensaje = "Error al eliminar el plato: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Eliminar")
                                }
                            }

                            // Mostrar ingredientes y botón para eliminarlos
                            plato.ingredientes.takeIf { it.isNotEmpty() }?.forEach { ingredienteCantidad ->
                                val ingrediente = ingredientes.find { it.id == ingredienteCantidad.ingredienteId }
                                ingrediente?.let {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(it.nombre)
                                            Text("Cantidad: ${ingredienteCantidad.cantidad}")
                                        }
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    try {
                                                        api.eliminarIngredienteDePlato(plato.id, it.id)
                                                        platos = platos.map { p ->
                                                            if (p.id == plato.id) {
                                                                p.copy(
                                                                    ingredientes = p.ingredientes.filterNot { ing -> ing.ingredienteId == it.id }
                                                                )
                                                            } else p
                                                        }
                                                    } catch (e: Exception) {
                                                        errorMensaje = "Error al eliminar el ingrediente: ${e.localizedMessage}"
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Eliminar Ingrediente")
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } else {
            Text("No hay platos registrados.")
        }

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun NuevoPlatoScreen(navController: NavController, restauranteId: Long) {
    val api = RetrofitClient.apiService
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var ingredientesSeleccionados by remember { mutableStateOf<MutableMap<Long, Double>>(mutableMapOf()) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    // Obtener los ingredientes del restaurante
    LaunchedEffect(restauranteId) {
        try {
            ingredientes = api.getIngredientesByRestaurante(restauranteId)
        } catch (e: HttpException) {
            mensajeError = "Error al cargar ingredientes: ${e.code()} - ${e.message()}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Esto habilita el scroll
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nuevo Plato", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar lista de ingredientes disponibles
        Text("Ingredientes", style = MaterialTheme.typography.bodyLarge)
        ingredientes.forEach { ingrediente ->
            var cantidadIngrediente by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(ingrediente.nombre)

                OutlinedTextField(
                    value = cantidadIngrediente,
                    onValueChange = {
                        cantidadIngrediente = it
                        // Actualizar la cantidad de ingrediente seleccionado
                        ingredientesSeleccionados[ingrediente.id] = cantidadIngrediente.toDoubleOrNull() ?: 0.0
                    },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(120.dp)
                )
            }
        }

        Button(
            onClick = {
                try {
                    val categoriaDTO = CategoriaDTO(nombre = categoria)
                    val platoDTO = PlatoDTO(
                        id = 0L,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        categoria = categoriaDTO,
                        restauranteId = restauranteId,
                        ingredientes = ingredientesSeleccionados.map { (id, cantidad) ->
                            IngredienteCantidadDTO(ingredienteId = id, cantidad = cantidad)
                        }
                    )

                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                api.crearPlato(platoDTO)
                            }
                            mensajeExito = "Plato creado correctamente"
                            navController.popBackStack()
                        } catch (e: Exception) {
                            mensajeError = "Error al crear plato: ${e.localizedMessage}"
                        }
                    }
                } catch (e: Exception) {
                    mensajeError = "Campos inválidos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        mensajeError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        mensajeExito?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
