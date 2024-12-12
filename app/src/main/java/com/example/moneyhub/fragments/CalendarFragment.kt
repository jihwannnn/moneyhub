package com.example.moneyhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentCalendarBinding
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.YearMonth

@AndroidEntryPoint // Hilt를 사용한 의존성 주입을 위한 어노테이션
class CalendarFragment : Fragment() {

    // Fragment들 간의 공유되는 Viewmodel을 activityModels()를 통해 가져옴
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCalendarView()
        observeTransactions()  // ViewModel의 데이터 변화를 관찰하기 시작
        observeCurrentYearMonth()

    }

    // RecyclerView 초기화 및 설정
    private fun setupRecyclerView() {
        adapter = TransactionAdapter(emptyList(), true, true)
        binding.transactionList.apply {
            adapter = this@CalendarFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCalendarView() {
        val dailyTotals = mutableMapOf<String, Pair<Double, Double>>() // 날짜별 <수입, 지출> 맵

        val transactions = sharedViewModel.filteredHistories.value
        // 각 날짜의 수입/지출 총액 계산
        transactions.forEach { transaction ->
            transaction.let {
                val currentPair = dailyTotals[DateUtils.millisToDate(it.payDate)] ?: Pair(0.0, 0.0)
                dailyTotals[DateUtils.millisToDate(it.payDate)] = when {
                    it.type -> Pair(currentPair.first + it.amount, currentPair.second)
                    else -> Pair(currentPair.first, currentPair.second + it.amount)
                }
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            // 선택된 월 정보 -> 뷰모델로
            val selectedYearMonth = YearMonth.of(year, month + 1)   // 월은 0부터 시작하니까
            sharedViewModel.setCurrentYearMonth(selectedYearMonth)

            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // 선택된 날짜의 거래 내역 필터링
            val transactionsForDate = transactions.filter { DateUtils.millisToDate(it.payDate) == selectedDate }
            adapter = TransactionAdapter(transactionsForDate, true, true)
            binding.transactionList.adapter = adapter

            // 선택된 날짜의 수입/지출 표시
            val totals = dailyTotals[selectedDate] ?: Pair(0.0, 0.0)
            binding.selectedDateIncome.apply {
                text = if (totals.first > 0) "일일 수입: ${String.format("₩%,d", totals.first.toInt())}" else ""
                setTextColor(ContextCompat.getColor(requireContext(), R.color.moneyGreenThick))
            }

            binding.selectedDateExpense.apply {
                text = if (totals.second > 0) "일일 지출: ${String.format("₩%,d", totals.second.toInt())}" else ""
                setTextColor(ContextCompat.getColor(requireContext(), R.color.moneyRed))
            }
        }
    }

    private fun updateMonthlyTotals(transactions: List<Transaction>) {
        var monthlyIncome = 0L
        var monthlyExpense = 0L

        // 모든 거래내역을 순회하면서 수입과 지출 합계 계산
        transactions.forEach { transaction ->
            if (transaction.type) {  // type이 true면 수입
                monthlyIncome += transaction.amount
            } else {  // type이 false면 지출
                monthlyExpense += transaction.amount  // 지출은 음수로 저장되어 있으므로 양수로 변환
            }
        }

        // 계산된 총액을 TextView에 표시
        binding.textViewIncome.text = String.format("₩%,d", monthlyIncome)  // 천단위 구분자(,) 포함
        binding.textViewExpense.text = String.format("₩%,d", monthlyExpense)
    }

    private fun observeCurrentYearMonth() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentYearMonth.collect { yearMonth ->

                    // 변경된 YearMonth를 기반으로 캘린더 업데이트
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(yearMonth.year, yearMonth.monthValue - 1, 1) // 월은 0부터 시작
                    binding.calendarView.date = calendar.timeInMillis
                }
            }
        }
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.filteredHistories.collect { transactions ->
                    updateMonthlyTotals(transactions)

                    // 현재 캘린더뷰에서 선택된 날짜 가져오기
                    val selectedDate = binding.calendarView.date

                    // 해당 날짜의 거래 내역만 필터링
                    val filteredForDate = transactions.filter {
                        DateUtils.isSameDay(it.payDate, selectedDate)
                    }

                    // 필터링된 데이터로 새 어댑터 생성
                    adapter = TransactionAdapter(
                        filteredForDate,  // 필터링된 거래 내역
                        true,            // 달력 보기 모드
                        true             // 캘린더 스타일 적용
                    )

                    // RecyclerView에 새 어댑터 설정
                    binding.transactionList.adapter = adapter

                    setupCalendarView()
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.updating()
    }
}