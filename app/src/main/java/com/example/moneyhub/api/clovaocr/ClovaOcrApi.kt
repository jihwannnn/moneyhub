package com.example.moneyhub.api.clovaocr

import okhttp3.MultipartBody
import retrofit2.http.*

interface ClovaOcrApi {
    @Multipart
    @POST("/v1/vision/text")
    suspend fun recognizeText(
        @Header("X-OCR-SECRET") secretKey: String, // Secret Key 헤더
        @Part image: MultipartBody.Part            // 이미지 파일
    ): OcrResponse
}