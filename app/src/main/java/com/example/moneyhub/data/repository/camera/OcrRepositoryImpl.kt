package com.example.moneyhub.data.repository.camera

import com.example.moneyhub.api.clovaocr.ClovaOcrApi
import com.example.moneyhub.api.clovaocr.OcrResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    private val api: ClovaOcrApi
) : OcrRepository {
    override suspend fun recognizeText(
        secretKey: String,
        message: RequestBody,
        file: MultipartBody.Part
    ): OcrResponse {
        return api.recognizeText(secretKey, message, file)
    }
}