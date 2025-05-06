package com.mauricio.helloworld

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


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

                        composable("verificarCodigo/{empleadoId}") { backStackEntry ->
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLong() ?: 0L
                            VerificacionCodigoScreen(navController, empleadoId)
                        }


                        composable("verificar") {
                            val empleadoId = it.savedStateHandle?.get<Long>("empleadoId") ?: -1
                            VerificarCodigoDialog(
                                empleadoId = empleadoId,
                                onCodigoCorrecto = { navController.navigate("main") },
                                onCancel = { navController.popBackStack() }
                            )
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
                            val empleadoId = backStackEntry.arguments?.getString("empleadoId")?.toLongOrNull() ?: -1
                            val restauranteId = backStackEntry.arguments?.getString("restauranteId")?.toLongOrNull() ?: -1
                            EmpleadoScreen(navController, empleadoId, restauranteId)
                        }


                    }
                }
            }
        }
    }
}
