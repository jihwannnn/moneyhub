package com.example.moneyhub.api.clovaocr

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://0uqgp61nqk.apigw.ntruss.com/"

    val api: ClovaOcrApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClovaOcrApi::class.java)
    }
}