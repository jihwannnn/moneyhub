package com.example.moneyhub.api.clovaocr

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ClovaOcrApi {
    @Multipart
    @POST("custom/v1/36618/94b3814bb3fdbb6a5a7be1f643a4718bbacdcca2a0fda7d8a5166b176fb1502a/general")
    suspend fun recognizeText(
        @Header("X-OCR-SECRET") secretKey: String,
        @Part("message") message: RequestBody,
        @Part file: MultipartBody.Part
    ): OcrResponse
}