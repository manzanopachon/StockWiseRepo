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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mauricio.stockwise.R
import kotlinx.coroutines.launch

@Composable
fun BotonAnimadoEntrar(onClick: () -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "animaciones")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsacion"
    )

    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 700f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    var clickScale by remember { mutableStateOf(1f) }
    val animatedClickScale by animateFloatAsState(
        targetValue = clickScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "clickScale"
    )

    Box(
        modifier = Modifier
            .scale(pulseScale * animatedClickScale)
            .width(280.dp)
            .height(72.dp)
            .drawBehind {
                // Halo animado
                drawRoundRect(
                    color = Color(0xFF80CBC4).copy(alpha = haloAlpha),
                    topLeft = Offset(-12f, -12f),
                    size = size.copy(width = size.width + 24f, height = size.height + 24f),
                    cornerRadius = CornerRadius(28.dp.toPx())
                )

                // Efecto shimmer
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX + 160f, 0f)
                )
                drawRoundRect(
                    brush = shimmerBrush,
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF00796B), Color(0xFF004D40))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                coroutineScope.launch {
                    clickScale = 0.92f
                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    MediaPlayer.create(context, R.raw.applepay).start()
                    onClick()
                    clickScale = 1f
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "👨‍🍳",
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "Entrar como Empleado",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )
        }
    }
}
