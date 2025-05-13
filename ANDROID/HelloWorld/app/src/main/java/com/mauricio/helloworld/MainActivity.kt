package com.mauricio.helloworld

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mauricio.helloworld.ui.theme.HelloWorldTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
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
                                onClienteClick = { /* ir a pantalla de cliente */ },
                                onEmpleadoClick = { navController.navigate("empleadoOptions") }
                            )
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

                        composable("verificarCodigo/{empleadoId}/{nombreEmpleado}") {
                            val empleadoId = it.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            val nombreEmpleadoRaw = it.arguments?.getString("nombreEmpleado") ?: "Desconocido"
                            val nombreEmpleado = URLDecoder.decode(nombreEmpleadoRaw,   StandardCharsets.UTF_8.toString())

                            VerificacionCodigoScreen(navController, empleadoId, nombreEmpleado)
                        }



                        composable("bienvenida/{nombreEmpleado}/{empleadoId}") { backStackEntry ->
                            val nombreEmpleado = backStackEntry.arguments?.getString("nombreEmpleado") ?: "Desconocido"
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            PantallaBienvenida(navController, nombreEmpleado, empleadoId)
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
                        composable("platoScreen/{empleadoId}/{restauranteId}") { backStackEntry ->
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLongOrNull() ?: 0L
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLongOrNull() ?: 0L

                            if (empleadoId == 0L || restauranteId == 0L) {
                                // Maneja el caso de IDs inválidos (por ejemplo, mostrar un mensaje de error)
                                Log.e("PlatoScreen", "Empleado o restaurante no válidos: empleadoId=$empleadoId, restauranteId=$restauranteId")
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
