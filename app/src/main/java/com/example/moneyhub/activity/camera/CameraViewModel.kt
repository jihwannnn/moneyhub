package com.example.moneyhub.activity.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.api.clovaocr.OcrResponse
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.camera.OcrRepository
import com.example.moneyhub.data.repository.transaction.TransactionRepository
import com.example.moneyhub.model.Transaction
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
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _ocrResult = MutableStateFlow<List<String>>(emptyList())
    val ocrResult: StateFlow<List<String>> = _ocrResult

    // OCR 성공 후 만들어진 Transaction을 담을 StateFlow
    private val _finalTransaction = MutableStateFlow<Transaction?>(null)
    val finalTransaction: StateFlow<Transaction?> = _finalTransaction

    fun callClovaOcrApi(imagePath: String, secretKey: String, originTransaction: Transaction) {
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

                // OCR API 호출
                val response = ocrRepository.recognizeText(secretKey, messageBody, filePart)

                // 결과 처리
                val detectedTexts = response.images.flatMap { it.fields }.map { it.inferText }
                _ocrResult.value = detectedTexts

                // OCR 결과를 바탕으로 Transaction 생성
                val updatedTransaction = parseOcrResponseToTransaction(response, originTransaction)

                // Firebase에 Transaction 업데이트 (verified = true, amount 등 업데이트)
                // 여기선 gid가 ""로 되어있는데, 실제 gid를 어디선가 가져와야 함(현재 코드에선 gid 정보 없음)
                val gid = updatedTransaction.gid.ifEmpty { "default_group_id" }
                transactionRepository.modifyTransaction(gid, updatedTransaction)

                _finalTransaction.value = updatedTransaction

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

    /**
     * OCR 결과를 바탕으로 Transaction을 업데이트하는 함수
     * 기존 transaction(인텐트로 받은 값)과 OCR 결과를 합쳐서 최종 Transaction을 만든다.
     */
    private fun parseOcrResponseToTransaction(ocrResponse: OcrResponse, originTransaction: Transaction): Transaction {
        val fields = ocrResponse.images.firstOrNull()?.fields ?: emptyList()
        val allTexts = fields.map { it.inferText }

        // title 추출: 일단 지금 영수증 기반 하드코딩임.
        val title = allTexts.find { it.contains("봉자막창") } ?: originTransaction.title

        // category: 일단 외식으로 하드코딩함
        val category = "외식"

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
            title = title,
            category = category,
            type = type,
            amount = amount,
            content = content,
            payDate = payDate,
            verified = true
        )
    }
}