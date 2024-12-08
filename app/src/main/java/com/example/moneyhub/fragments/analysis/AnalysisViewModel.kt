package com.example.moneyhub.fragments.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.transaction.TransactionRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.YearMonth

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)

    // 현재 선택된 년월을 저장하는 상태
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth

    // 차트 데이터들...
    data class CategoryAmount(
        val name: String,
        val amount: Long,
        val percentage: Float = 0f
    )

    private val _categoryData = MutableStateFlow<List<CategoryAmount>>(emptyList())
    val categoryData: StateFlow<List<CategoryAmount>> = _categoryData

    data class DailyAmount(
        val day: Int,
        val amount: Long
    )

    private val _dailyData = MutableStateFlow<List<DailyAmount>>(emptyList())
    val dailyData: StateFlow<List<DailyAmount>> = _dailyData

    private val _totalIncome = MutableStateFlow(0L)
    val totalIncome: StateFlow<Long> = _totalIncome

    private val _totalExpense = MutableStateFlow(0L)
    val totalExpense: StateFlow<Long> = _totalExpense

    init {
        loadCurrentUser()
    }

    // 월 이동 함수들
    fun moveToNextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        refreshData()
    }

    fun moveToPreviousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        refreshData()
    }

    // 월 표시 텍스트(예: "2024 Dec") 반환
    fun getMonthDisplayText(): String {
        val year = _currentYearMonth.value.year
        val month = _currentYearMonth.value.month.name.take(3)
        return "$year $month"
    }

    private fun loadCurrentUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
        refreshData()
    }

    fun refreshData() {
        _currentUser.value?.let { user ->
            loadTransactionsByMonth(user.currentGid)
        }
    }

    private fun loadTransactionsByMonth(gid: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                println("DEBUG: Starting to load transactions for gid: $gid")

                val yearMonth = _currentYearMonth.value
                val epochDay = yearMonth.atDay(1).toEpochDay()
                val timestamp = epochDay * 24 * 60 * 60 * 1000  // 밀리초로 변환

                println("DEBUG: Calculated timestamp: $timestamp for ${yearMonth.year}-${yearMonth.monthValue}")

                transactionRepository.getTransactionsByMonth(gid, timestamp).fold(
                    onSuccess = { transactions ->
                        println("DEBUG: Successfully loaded ${transactions.size} transactions")
                        println("DEBUG: First few transactions: ${
                            transactions.take(3).map {
                                "Date: ${DateUtils.millisToDate(it.payDate)}, Amount: ${it.amount}"
                            }
                        }")

                        updateChartData(transactions)
                        _uiState.update { it.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        ) }
                    },
                    onFailure = { throwable ->
                        println("ERROR: Failed to load transactions: ${throwable.message}")
                        throwable.printStackTrace()
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = throwable.message
                        ) }
                    }
                )
            } catch (e: Exception) {
                println("ERROR: Unexpected error in loadTransactionsByMonth: ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    private fun updateChartData(transactions: List<Transaction>) {
        println("DEBUG: Starting to update chart data with ${transactions.size} transactions")
        try {
            calculateTotals(transactions)
            updateCategoryData(transactions)
            updateDailyData(transactions)
            println("DEBUG: Chart data update completed successfully")
        } catch (e: Exception) {
            println("ERROR: Failed to update chart data: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var income = 0L
        var expense = 0L

        transactions.forEach { transaction ->
            if (transaction.type) { // 수입
                income += transaction.amount
            } else { // 지출
                expense += transaction.amount
            }
        }

        _totalIncome.value = income
        _totalExpense.value = expense
    }

    private fun updateCategoryData(transactions: List<Transaction>) {
        // 지출 거래만 필터링
        val expenseTransactions = transactions.filter { !it.type }

        // 카테고리별 합계 계산
        val categoryAmounts = expenseTransactions
            .groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

        // 총 지출 계산
        val totalExpense = categoryAmounts.values.sum().toFloat()

        // 비율 계산 및 정렬
        _categoryData.value = categoryAmounts
            .map { (category, amount) ->
                CategoryAmount(
                    name = category,
                    amount = amount,
                    percentage = if (totalExpense > 0) (amount / totalExpense) * 100 else 0f
                )
            }
            .sortedByDescending { it.amount }
    }

    private fun updateDailyData(transactions: List<Transaction>) {
        val currentYearMonth = YearMonth.now()
        val daysInMonth = currentYearMonth.lengthOfMonth()

        val dailyAmounts = (1..daysInMonth).map { day ->
            val dayTotal = transactions
                .filter { transaction ->
                    val transactionDay = java.time.Instant
                        .ofEpochMilli(transaction.payDate)
                        .atZone(java.time.ZoneId.systemDefault())
                        .dayOfMonth
                    transactionDay == day
                }
                .sumOf { if (it.type) it.amount else -it.amount }

            DailyAmount(day, dayTotal)
        }

        _dailyData.value = dailyAmounts
    }
}