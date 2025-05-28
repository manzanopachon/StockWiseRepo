package com.mauricio.helloworld.screens

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mauricio.helloworld.model.IngredienteRequest
import com.mauricio.helloworld.model.IngredienteResponse
import com.mauricio.helloworld.retrofit.RetrofitClient
import com.mauricio.helloworld.ui.theme.Caveat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Añadir Ingrediente",
                fontFamily = Caveat,
                fontSize = 35.sp,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00796B),
                modifier = Modifier.padding(vertical = 24.dp)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("📷 Tomar Foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💾 Guardar Ingrediente")
            }
        }
    }
}
