package com.mauricio.stockwise.screens.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
fun formatearFecha(fechaHora: String): Pair<String, String> {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val parsed = LocalDateTime.parse(fechaHora, formatter)
        val fecha = parsed.toLocalDate().toString()
        val hora = parsed.toLocalTime().withNano(0).toString() // hh:mm:ss sin milisegundos
        Pair(fecha, hora)
    } catch (e: Exception) {
        Pair("Fecha inválida", "")
    }
}
