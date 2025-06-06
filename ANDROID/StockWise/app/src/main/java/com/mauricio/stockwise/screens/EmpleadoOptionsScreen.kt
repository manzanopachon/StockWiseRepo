package com.mauricio.stockwise.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mauricio.stockwise.screens.utils.BotonRegistrarseEstilizado
import com.mauricio.stockwise.ui.theme.Caveat
import com.mauricio.stockwise.ui.theme.Oswald
import com.mauricio.stockwise.ui.theme.RestaurantePrimary
import com.mauricio.stockwise.ui.theme.RestauranteSecondary
import com.mauricio.stockwise.ui.theme.RestauranteText

@Composable
fun EmpleadoOptionsScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            RestaurantePrimary,
            Color(0xFFB2EBF2),
            Color(0xFF80DEEA)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient),
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
                text = "Bienvenido a\nStockWise 🍽️",
                fontFamily = Oswald,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B),
                lineHeight = 45.sp, //
            )

            BotonAnimadoEntrar(onClick = onLoginClick)

            Spacer(modifier = Modifier.height(24.dp))

            BotonRegistrarseEstilizado(onClick = onRegisterClick)
        }
    }
}
