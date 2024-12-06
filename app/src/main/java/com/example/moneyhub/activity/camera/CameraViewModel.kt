package com.example.moneyhub.activity.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.camera.OcrRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: OcrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _ocrResult = MutableStateFlow<List<String>>(emptyList())
    val ocrResult: StateFlow<List<String>> = _ocrResult

    fun callClovaOcrApi(imagePath: String, secretKey: String) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            try {
                val requestJson = mapOf(
                    "version" to "V2",
                    "requestId" to UUID.randomUUID().toString(),
                    "timestamp" to System.currentTimeMillis(),
                    "images" to listOf(mapOf("format" to "jpg", "name" to "demo"))
                )

                val jsonString = Gson().toJson(requestJson)
                val messageBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

                // 이미지 파일 RequestBody 생성
                val file = File(imagePath)
                val fileRequestBody = file.asRequestBody("image/jpeg".toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

                // Repository 호출
                val response = repository.recognizeText(secretKey, messageBody, filePart)

                // 결과 처리
                val detectedTexts = response.images.flatMap { it.fields }.map { it.inferText }
                _ocrResult.value = detectedTexts
                _uiState.value = UiState(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = UiState(
                    isLoading = false,
                    isSuccess = false,
                    error = "OCR 호출 실패: ${e.message}"
                )
                Log.d("ITM", "OCR 호출 실패: ${e.message}")
            }
        }
    }
}