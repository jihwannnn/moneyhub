package com.example.moneyhub.fragments.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 홈 화면의 월 이동과 관련된 상태를 관리하는 ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // 현재 선택된 년월을 저장하는 상태
    // StateFlow는 상태를 관찰 가능한 형태로 만들어주는 Kotlin의 Flow API
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    // 월 표시 텍스트(예: "Oct")를 반환하는 함수
    fun getMonthDisplayText(): String {
        val year = getCurrentYear()
        val month = _currentYearMonth.value.month.name.take(3)
        return "$year $month"  // 예: "2024 Dec"
    }

    // 다음 달로 이동하는 함수
    fun moveToNextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
    }

    // 이전 달로 이동하는 함수
    fun moveToPreviousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
    }

    // 현재 선택된 연도를 반환하는 함수
    fun getCurrentYear(): Int {
        return _currentYearMonth.value.year
    }

    // 현재 선택된 월을 반환하는 함수 (1-12)
    fun getCurrentMonth(): Int {
        return _currentYearMonth.value.monthValue
    }
}

