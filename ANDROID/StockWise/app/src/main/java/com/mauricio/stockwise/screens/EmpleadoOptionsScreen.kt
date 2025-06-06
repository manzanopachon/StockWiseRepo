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
                text = "👨‍🍳 Opciones de\nEmpleado",
                fontFamily = Oswald,
                color = RestauranteText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 42.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            )

            val buttonModifier = Modifier
                .width(260.dp)
                .height(64.dp)

            val shape = RoundedCornerShape(16.dp)

            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RestauranteSecondary,
                    contentColor = Color.White
                ),
                shape = shape,
                modifier = buttonModifier
            ) {
                Text(text = "🔓 Iniciar Sesión", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White
                ),
                shape = shape,
                modifier = buttonModifier
            ) {
                Text(text = "📝 Registrarse", fontSize = 20.sp)
            }
        }
    }
}
