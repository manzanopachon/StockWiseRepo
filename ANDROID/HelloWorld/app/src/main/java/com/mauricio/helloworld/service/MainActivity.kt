package com.mauricio.helloworld.service

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mauricio.helloworld.screens.*
import com.mauricio.helloworld.ui.theme.HelloWorldTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HelloWorldTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(

                                onEmpleadoClick = { navController.navigate("empleadoOptions") }
                            )
                        }

                        composable("verCarta/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                            val url = Uri.decode(encodedUrl)
                            WebViewScreen(url)
                        }

                        composable("empleadoOptions") {
                            EmpleadoOptionsScreen(
                                onLoginClick = { navController.navigate("login") },
                                onRegisterClick = { navController.navigate("registro") }
                            )
                        }

                        composable("registro") {
                            RegistroScreen(onRegisterSuccess = {
                                navController.popBackStack("empleadoOptions", false)
                            })
                        }

                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        composable("verificarCodigo/{empleadoId}/{nombreEmpleado}/{restauranteId}") {
                            val empleadoId = it.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            val nombreEmpleadoRaw = it.arguments?.getString("nombreEmpleado") ?: "Desconocido"
                            val nombreEmpleado = URLDecoder.decode(nombreEmpleadoRaw, StandardCharsets.UTF_8.toString())
                            val restauranteId = it.arguments?.getString("restauranteId")?.toLong() ?: 0L

                            VerificacionCodigoScreen(navController, empleadoId, nombreEmpleado)
                        }

                        composable("bienvenida/{nombreEmpleado}/{empleadoId}") { backStackEntry ->
                            val nombreEmpleado = backStackEntry.arguments?.getString("nombreEmpleado") ?: "Desconocido"
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            PantallaBienvenida(navController, nombreEmpleado, empleadoId)
                        }

                        composable("verPedidos/{empleadoId}/{restauranteId}") { backStackEntry ->
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLong() ?: 0L
                            PedidosScreen(navController, restauranteId)
                        }

                        composable("empleadoDatos/{empleadoId}") { backStackEntry ->
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            DatosEmpleadoScreen(navController, empleadoId)
                        }

                        composable("ingredientes/{empleadoId}/{restauranteId}") { backStackEntry ->
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLong() ?: 0L
                            EmpleadoScreen(navController, empleadoId, restauranteId)
                        }

                        composable("nuevoIngrediente/{restauranteId}") { backStackEntry ->
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLong() ?: 0L
                            IngredienteScreen(navController, restauranteId)
                        }

                        composable("ingredienteDetalle/{ingredienteId}") {
                            val ingredienteId = it.arguments?.getString("ingredienteId")?.toLongOrNull() ?: return@composable
                            DetalleIngredienteScreen(navController, ingredienteId)
                        }

                        composable("ingredienteEditar/{ingredienteId}") {
                            val ingredienteId = it.arguments?.getString("ingredienteId")?.toLongOrNull() ?: return@composable
                            EditarIngredienteScreen(navController, ingredienteId)
                        }

                        composable("platoScreen/{empleadoId}/{restauranteId}") { backStackEntry ->
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLongOrNull() ?: 0L
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLongOrNull() ?: 0L

                            if (empleadoId == 0L || restauranteId == 0L) {
                                Log.e("PlatoScreen", "IDs inválidos: empleadoId=$empleadoId, restauranteId=$restauranteId")
                            } else {
                                PlatoScreen(navController, empleadoId, restauranteId)
                            }
                        }

                        composable("nuevoPlato/{restauranteId}") { backStackEntry ->
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLongOrNull()
                            restauranteId?.let {
                                NuevoPlatoScreen(navController, it)
                            }
                        }
                    }
                }
            }
        }
    }
}
