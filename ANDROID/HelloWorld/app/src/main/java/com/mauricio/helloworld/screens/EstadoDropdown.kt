package com.mauricio.helloworld.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EstadoDropdown(
    pedidoId: String,
    estadoActual: String,
    context: Context,
    onEstadoSeleccionado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0F2F1),
                contentColor = Color(0xFF00796B)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = estadoActual,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("PENDIENTE", "EN_PROCESO", "FINALIZADO").forEach { estado ->
                DropdownMenuItem(
                    text = {
                        Text(
                            estado,
                            color = when (estado) {
                                "FINALIZADO" -> Color(0xFF4CAF50)
                                "EN_PROCESO" -> Color(0xFFFF9800)
                                "PENDIENTE" -> Color(0xFF2196F3)
                                else -> Color.Black
                            },
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        onEstadoSeleccionado(estado)
                        expanded = false
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}
