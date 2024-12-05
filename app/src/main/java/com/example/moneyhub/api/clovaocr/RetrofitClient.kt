package com.example.moneyhub.api.clovaocr

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://0uqgp61nqk.apigw.ntruss.com/custom/v1/36533/94b3814bb3fdbb6a5a7be1f643a4718bbacdcca2a0fda7d8a5166b176fb1502a/infer"

    val api: ClovaOcrApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClovaOcrApi::class.java)
    }
}