package com.mauricio.stockwise.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.mauricio.stockwise.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaPrincipal(navController: NavController, empleadoId: Long, restauranteId: Long) {
    val api = RetrofitClient.apiService
    var restauranteRealId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(empleadoId) {
        try {
            val empleado = withContext(Dispatchers.IO) {
                api.obtenerEmpleadoPorId(empleadoId)
            }
            restauranteRealId = empleado.restaurante.id
            Log.d("PantallaPrincipal", "Cargado restauranteId=${restauranteRealId}")
        } catch (e: Exception) {
            Log.e("PantallaPrincipal", "Error al obtener restaurante: ${e.message}")
            restauranteRealId = null
        }
    }

    restauranteRealId?.let { restId ->
        val bottomNavController = rememberNavController()
        val items = listOf(
            BottomNavItem.Ingredientes,
            BottomNavItem.Platos,
            BottomNavItem.Pedidos,
            BottomNavItem.Empleado
        )

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF00796B), // fondo verde StockWise
                    contentColor = Color.White
                ) {
                    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = Color(0xFF004D40) // círculo detrás del ítem seleccionado
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Ingredientes.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Ingredientes.route) {
                    EmpleadoScreen(navController, empleadoId, restId)
                }
                composable(BottomNavItem.Platos.route) {
                    PlatoScreen(navController, empleadoId, restId)
                }
                composable(BottomNavItem.Pedidos.route) {
                    PedidosScreen(navController, restId)
                }
                composable(BottomNavItem.Empleado.route) {
                    DatosEmpleadoScreen(navController, empleadoId)
                }
            }
        }
    } ?: run {
        Text("Cargando datos del restaurante...", color = MaterialTheme.colorScheme.primary)
    }
}
