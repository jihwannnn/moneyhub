package com.example.moneyhub.data.repository.camera

import com.example.moneyhub.api.clovaocr.OcrResponse
import okhttp3.MultipartBody

interface OcrRepository {
    suspend fun recognizeText(secretKey: String, image: MultipartBody.Part): OcrResponse
}