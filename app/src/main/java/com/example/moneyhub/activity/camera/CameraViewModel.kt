package com.example.moneyhub.activity.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.camera.OcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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
                val file = File(imagePath)
                val mediaType = "image/jpeg".toMediaType()
                val requestBody = RequestBody.create(mediaType, file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

                // API 호출
                val response = repository.recognizeText(secretKey, imagePart)

                // 결과 업데이트
                val detectedTexts = response.images.flatMap { image ->
                    image.fields.map { it.inferText }
                }
                _ocrResult.value = detectedTexts
                _uiState.value = UiState(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = UiState(
                    isLoading = false,
                    isSuccess = false,
                    error = "OCR 호출 실패: ${e.message}"
                )
            }
        }
    }
}