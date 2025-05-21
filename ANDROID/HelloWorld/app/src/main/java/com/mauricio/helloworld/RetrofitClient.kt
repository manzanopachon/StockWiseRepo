package com.mauricio.helloworld

import ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    //private const val BASE_URL = "http://192.168.5.38:8080/" // Usa 10.0.2.2 para localhost desde el emulador Android
    //192.168.5.38
    //192.168.1.28
    //private const val BASE_URL = "http://192.168.5.53:8080/" //SOULON
    //private const val BASE_URL = "http://192.168.1.28:8080/" //CASA
    private const val BASE_URL = "https://stockwise.fly.dev/" //FINAL


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
