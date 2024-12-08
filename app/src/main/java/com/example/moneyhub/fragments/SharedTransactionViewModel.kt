package com.example.moneyhub.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.transaction.TransactionRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Year
import java.time.YearMonth
import javax.inject.Inject


// 거래 내역 데이터를 관리하고 공유하는 ViewModel
@HiltViewModel
class SharedTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    // UI 상태(로딩, 에러 등)를 관리하는 상태
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // 전체 거래 내역 목록을 저장하는 상태
    private val _histories = MutableStateFlow<List<Transaction>>(emptyList())
    val histories: StateFlow<List<Transaction>> = _histories.asStateFlow()

    // 전체 예산 목록을 저장하는 상태
    private val _budgets = MutableStateFlow<List<Transaction>>(emptyList())
    val budgets: StateFlow<List<Transaction>> = _budgets.asStateFlow()

    // 선택된 월에 해당하는 거래 내역만 필터링하여 저장하는 상태
    private val _filteredHistories = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredHistories: StateFlow<List<Transaction>> = _filteredHistories.asStateFlow()

    // 선택된 월에 해당하는 예산만 필터링하여 저장하는 상태
    private val _filteredBudgets = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredBudgets: StateFlow<List<Transaction>> = _filteredBudgets.asStateFlow()

    // 현재 로그인한 사용자 정보를 저장하는 상태
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()


    init {
        println("DEBUG: ViewModel initialized")
        viewModelScope.launch(Dispatchers.Main.immediate) {  // Main.immediate로 변경

            loadUser()
        }
    }

    fun setCurrentYearMonth(yearMonth: YearMonth) {
        _currentYearMonth.value = yearMonth
    }


    private fun filterCurrentMonthTransactions() {
        val currentYearMonth = YearMonth.now()
        val startOfMonth = DateUtils.getStartOfMonth(currentYearMonth.atDay(1).toEpochDay() * 86400000)
        val endOfMonth = DateUtils.getEndOfMonth(currentYearMonth.atDay(1).toEpochDay() * 86400000)

        println("DEBUG: Filter range - Start: ${DateUtils.millisToDate(startOfMonth)} 00:00:00.000")
        println("DEBUG: Filter range - End: ${DateUtils.millisToDate(endOfMonth)} 23:59:59.999")

        _filteredHistories.value = _histories.value.filter { transaction ->
            val inRange = transaction.payDate in startOfMonth..endOfMonth
            println("DEBUG: Transaction ${DateUtils.millisToDate(transaction.payDate)} ${if (inRange) "IS" else "is NOT"} in range")
            inRange
        }
    }



    // 현재 사용자 정보를 로드하고 해당 사용자의 거래 내역을 가져오는 함수
    private fun loadUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
        _currentUser.value?.let { user ->
            loadTransactions(user.currentGid)
        }
    }

    // 특정 월의 거래 내역만 필터링하는 함수
    fun updateMonthlyTransactions(yearMonth: YearMonth) {
        viewModelScope.launch {
            // 해당 월의 시작일시와 종료일시 계산
            val startOfMonth = DateUtils.getStartOfMonth(yearMonth.atDay(1).toEpochDay() * 86400000)
            val endOfMonth = DateUtils.getEndOfMonth(yearMonth.atDay(1).toEpochDay() * 86400000)

            println("DEBUG: Date range = ${DateUtils.millisToDate(startOfMonth)} to ${DateUtils.millisToDate(endOfMonth)}")


            // 거래 내역 필터링
            val filtered = _histories.value.filter { transaction ->
                println("DEBUG: Checking transaction date = ${DateUtils.millisToDate(transaction.payDate)}")
                transaction.payDate in startOfMonth..endOfMonth
            }

            println("DEBUG: Filtered transactions size = ${filtered.size}")
            _filteredHistories.value = filtered

            // 해당 기간에 속하는 예산만 필터링
            _filteredBudgets.value = _budgets.value.filter { transaction ->
                transaction.payDate in startOfMonth..endOfMonth
            }
        }
    }

    // 데이터베이스에서 거래 내역을 가져오는 함수
    private fun loadTransactions(gid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 확인된(verified) 거래 내역 가져오기
                transactionRepository.getTransactions(gid, true).fold(
                    onSuccess = { transactions ->
                        _histories.value = transactions.sortedByDescending { it.payDate }
                        _uiState.update { it.copy(isLoading = false) }
                        filterCurrentMonthTransactions() // 여기서 한 번만 필터링
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = throwable.message
                        ) }
                    }
                )

                // 예산(unverified) 거래 내역 가져오기
                transactionRepository.getTransactions(gid, false).fold(
                    onSuccess = { transactions ->
                        _budgets.value = transactions.sortedByDescending { it.payDate }
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(error = throwable.message) }
                    }
                )

                // 현재 월에 대한 초기 필터링 적용
//                YearMonth.now(): 현재 년월을 가져옴 (예: 2024년 12월)
//                updateMonthlyTransactions는 이 년월을 기준으로 해당 월에 해당하는 거래내역만 필터링
//                즉, 앱이 처음 실행될 때 현재 월의 거래내역만 보이도록 초기 필터링

                updateMonthlyTransactions(YearMonth.now())
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    // 새로운 거래 내역을 추가하는 함수
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _currentUser.value?.currentGid?.let { gid ->
                _uiState.update { it.copy(isLoading = true) }

                transactionRepository.addTransaction(gid, transaction).fold(
                    onSuccess = {
                        loadTransactions(gid)
                        _uiState.update { it.copy(isSuccess = true) }
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = throwable.message
                        ) }
                    }
                )
            }
        }
    }
}