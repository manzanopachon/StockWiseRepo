package com.mauricio.stockwise.screens.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonRegistrarseEstilizado(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(72.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF26A69A), Color(0xFF4DB6AC)) // más claros
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📝",
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "Registrarse",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )
        }
    }
}
