package com.mauricio.helloworld.retrofit

import com.mauricio.helloworld.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    //private const val BASE_URL = "http://192.168.5.38:8080/api/"
    //private const val BASE_URL = "http://192.168.5.53:8080/api/"
    //private const val BASE_URL = "http://192.168.1.28:8080/api/"

    private const val BASE_URL = "https://stockwise.fly.dev/" //FINAL

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
