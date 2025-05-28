package com.mauricio.stockwise.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mauricio.stockwise.R

@Composable
fun BotonAnimadoEntrar(onClick: () -> Unit) {
    val context = LocalContext.current

    // Transiciones animadas
    val infiniteTransition = rememberInfiniteTransition(label = "animaciones")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsacion"
    )

    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Botón animado con sombra y brillo
    Box(
        modifier = Modifier
            .scale(scale)
            .width(280.dp)
            .height(72.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp)) // sombra elegante
            .drawBehind {
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX + 300f, size.height)
                )
                drawRoundRect(shimmerBrush, cornerRadius = CornerRadius(16.dp.toPx()))
            }
            .background(Color(0xFF00796B), shape = RoundedCornerShape(16.dp))
            .clickable {
                MediaPlayer.create(context, R.raw.applepay).start()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👨‍🍳 Entrar como Empleado",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp
        )
    }
}
