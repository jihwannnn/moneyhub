package com.example.moneyhub.data.repository.camera

import com.example.moneyhub.api.clovaocr.OcrResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface OcrRepository {
    suspend fun recognizeText(
        secretKey: String,
        message: RequestBody,
        file: MultipartBody.Part
    ): OcrResponse
}