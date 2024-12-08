package com.example.moneyhub.activity.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.api.clovaocr.OcrResponse
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.camera.OcrRepository
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.model.sessions.RegisterTransactionSession
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val ocrRepository: OcrRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _ocrResult = MutableStateFlow<List<String>>(emptyList())
    val ocrResult: StateFlow<List<String>> = _ocrResult

    private val _originTransaction = MutableStateFlow(Transaction())
    val originTransaction: StateFlow<Transaction> = _originTransaction

    private val _finalTransaction = MutableStateFlow(Transaction())
    val finalTransaction: StateFlow<Transaction> = _finalTransaction

    init {
        loadOriginTransaction()
    }

    private fun loadOriginTransaction() {
        _originTransaction.value = RegisterTransactionSession.getCurrentTransaction().copy(verified = true)
        RegisterTransactionSession.setTransaction(_originTransaction.value)
    }

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

                val file = File(imagePath)
                val fileRequestBody = file.asRequestBody("image/jpeg".toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

                val response = ocrRepository.recognizeText(secretKey, messageBody, filePart)

                val detectedTexts = response.images.flatMap { it.fields }.map { it.inferText }
                _ocrResult.value = detectedTexts

                val updatedTransaction = parseOcrResponseToTransaction(response, _originTransaction.value)
                _finalTransaction.value = updatedTransaction

                RegisterTransactionSession.setTransaction(updatedTransaction)
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

    /**
     * OCR 결과를 바탕으로 Transaction을 업데이트하는 함수
     * 기존 transaction(인텐트로 받은 값)과 OCR 결과를 합쳐서 최종 Transaction을 만든다.
     */
    private fun parseOcrResponseToTransaction(ocrResponse: OcrResponse, originTransaction: Transaction): Transaction {
        val fields = ocrResponse.images.firstOrNull()?.fields ?: emptyList()
        val allTexts = fields.map { it.inferText }

        // type: 영수증이면 지출이라고 일단 봄
        val type = false

        // amount 추출 ("승인금액:" 패턴)
        val joinedText = allTexts.joinToString(" ")
        val amountRegex = Regex("승인금액[:]?\\s*([0-9,]+)")
        val amountMatch = amountRegex.find(joinedText)
        val amount = amountMatch?.groups?.get(1)?.value?.replace(",", "")?.toLongOrNull() ?: originTransaction.amount

        // 날짜 추출: 인덱스를 이용
        // 발행일시를 찾는다.
        val dateIndex = allTexts.indexOfFirst { it.contains("발행일시") }
        var payDate = originTransaction.payDate
        if (dateIndex != -1) {
            // dateIndex+2 라인: "2024-11-30"
            // dateIndex+3 라인: "21:"
            // dateIndex+4 라인: "06:"
            // dateIndex+5 라인: "31"
            // 이 값들이 모두 있는지 확인 필요
            if (dateIndex + 5 < allTexts.size) {
                val datePart = allTexts[dateIndex + 2] // "2024-11-30"
                val hourPart = allTexts[dateIndex + 3].replace(":", "") // "21"
                val minutePart = allTexts[dateIndex + 4].replace(":", "") // "06"
                val secondPart = allTexts[dateIndex + 5] // "31"

                val dateTimeString = "$datePart $hourPart:$minutePart:$secondPart"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val parsed = sdf.parse(dateTimeString)
                if (parsed != null) {
                    payDate = parsed.time
                }
            }
        }

        // content: 품명~주문합계 사이 추출
        val startIndex = allTexts.indexOfFirst { it.contains("품명") }
        val endIndex = allTexts.indexOfFirst { it.contains("주문합계") }
        val contentList = if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            allTexts.subList(startIndex + 1, endIndex)
        } else {
            emptyList()
        }
        val content = if (contentList.isNotEmpty()) contentList.joinToString(", ") else originTransaction.content

        // verified = true로 변경
        // tid, gid, authorId 등은 originTransaction에서 가져옴
        return originTransaction.copy(
            type = type,
            amount = amount,
            content = content,
            payDate = payDate,
        )
    }
}