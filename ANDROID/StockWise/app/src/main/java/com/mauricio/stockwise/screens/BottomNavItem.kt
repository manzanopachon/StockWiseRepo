package com.mauricio.stockwise.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Ingredientes : BottomNavItem("ingredientes", "Stock", Icons.Filled.List)
    object Platos : BottomNavItem("platos", "Platos", Icons.Filled.RestaurantMenu)
    object Pedidos : BottomNavItem("pedidos", "Pedidos", Icons.Filled.Receipt)
    object Empleado : BottomNavItem("empleado", "Empleado", Icons.Filled.Person)
}