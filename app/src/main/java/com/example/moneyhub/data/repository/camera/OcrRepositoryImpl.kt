package com.example.moneyhub.data.repository.camera

import com.example.moneyhub.api.clovaocr.ClovaOcrApi
import com.example.moneyhub.api.clovaocr.OcrResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    private val api: ClovaOcrApi
) : OcrRepository {
    override suspend fun recognizeText(secretKey: String, image: MultipartBody.Part): OcrResponse {
        return api.recognizeText(secretKey, image)
    }
}