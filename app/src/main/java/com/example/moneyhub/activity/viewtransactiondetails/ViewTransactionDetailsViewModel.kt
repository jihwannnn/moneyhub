package com.example.moneyhub.activity.viewtransactiondetails

import androidx.lifecycle.ViewModel
import com.example.moneyhub.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
// 거래 내역 상세보기를 위한 ViewModel 클래스
class ViewTransactionDetailsViewModel @Inject constructor() : ViewModel() {

    // 빈 거래 내역을 초기화
    private val _transaction = MutableStateFlow(Transaction())

    val transaction: StateFlow<Transaction> = _transaction.asStateFlow()

    // 거래 내역을 로드하는 함수
    // parameter로 받은 transaction을 내부 _transation에 저장
    fun loadTransaction(transaction: Transaction) {
        _transaction.value = transaction
    }
}