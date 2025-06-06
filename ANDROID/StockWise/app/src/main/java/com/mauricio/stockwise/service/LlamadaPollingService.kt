package com.mauricio.stockwise.service


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mauricio.stockwise.R
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LlamadaPollingService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        iniciarPolling()
    }

    private fun iniciarPolling() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://stockwise.fly.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        coroutineScope.launch {
            while (true) {
                try {
                    val llamadas = api.obtenerLlamadasPendientes()
                    for (llamada in llamadas) {
                        mostrarNotificacion(
                            llamada.id,
                            "🍽 Restaurante ${llamada.restauranteId}",
                            "La mesa ${llamada.mesaId} ha llamado al camarero"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(7000) // cada 7 segundos
            }
        }
    }

    private fun mostrarNotificacion(id: Long, titulo: String, mensaje: String) {
        val channelId = "llamadas_camarero"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Llamadas de camarero",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(R.drawable.ic_notification) // icono que tengas en res/drawable
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}
