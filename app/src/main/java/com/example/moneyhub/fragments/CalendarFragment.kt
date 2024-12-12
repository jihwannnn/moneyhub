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
import java.util.Calendar

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionAdapter

    // 날짜별 수입/지출 총액을 저장하는 맵
    private val dailyTotals = mutableMapOf<String, Pair<Long, Long>>()  // <날짜, <수입, 지출>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCalendarView()
        observeTransactions()
        observeCurrentYearMonth()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            transactions = emptyList(),
            isForBudget = false,
            isForCalendar = true
        )

        binding.transactionList.apply {
            adapter = this@CalendarFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 선택된 연/월 정보를 ViewModel에 전달
            val selectedYearMonth = YearMonth.of(year, month + 1)
            sharedViewModel.setCurrentYearMonth(selectedYearMonth)

            // 선택된 날짜의 거래 내역 표시
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            updateSelectedDateView(selectedDate)
        }
    }

    private fun updateSelectedDateView(selectedDate: String) {
        // 선택된 날짜의 수입/지출 총액 표시
        val totals = dailyTotals[selectedDate] ?: Pair(0L, 0L)

        // 수입 표시
        binding.selectedDateIncome.apply {
            text = if (totals.first > 0) {
                "일일 수입: ${String.format("₩%,d", totals.first)}"
            } else ""
            setTextColor(ContextCompat.getColor(requireContext(), R.color.moneyGreenThick))
        }

        // 지출 표시
        binding.selectedDateExpense.apply {
            text = if (totals.second > 0) {
                "일일 지출: ${String.format("₩%,d", totals.second)}"
            } else ""
            setTextColor(ContextCompat.getColor(requireContext(), R.color.moneyRed))
        }

        // 해당 날짜의 거래 내역 목록 업데이트
        val selectedTimestamp = DateUtils.dateToMillis(selectedDate) ?: return
        val transactionsForDate = sharedViewModel.filteredHistories.value.filter {
            DateUtils.isSameDay(it.payDate, selectedTimestamp)
        }
        adapter.updateData(transactionsForDate)
    }

    private fun updateMonthlyTotals(transactions: List<Transaction>) {
        var monthlyIncome = 0L
        var monthlyExpense = 0L

        transactions.forEach { transaction ->
            if (transaction.type) {
                monthlyIncome += transaction.amount
            } else {
                monthlyExpense += transaction.amount
            }
        }

        binding.textViewIncome.text = String.format("₩%,d", monthlyIncome)
        binding.textViewExpense.text = String.format("₩%,d", monthlyExpense)
    }

    private fun observeCurrentYearMonth() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentYearMonth.collect { yearMonth ->
                    // 캘린더 뷰의 표시 월 업데이트
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, yearMonth.year)
                        set(Calendar.MONTH, yearMonth.monthValue - 1)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    binding.calendarView.date = calendar.timeInMillis
                }
            }
        }
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.filteredHistories.collect { transactions ->
                    // 일별 수입/지출 총액 계산
                    dailyTotals.clear()
                    transactions.forEach { transaction ->
                        val dateStr = DateUtils.millisToDate(transaction.payDate)
                        val currentPair = dailyTotals[dateStr] ?: Pair(0L, 0L)
                        dailyTotals[dateStr] = if (transaction.type) {
                            Pair(currentPair.first + transaction.amount, currentPair.second)
                        } else {
                            Pair(currentPair.first, currentPair.second + transaction.amount)
                        }
                    }

                    // 월간 총액 업데이트
                    updateMonthlyTotals(transactions)

                    // 현재 선택된 날짜의 거래 내역 업데이트
                    val currentSelectedDate = DateUtils.millisToDate(binding.calendarView.date)
                    updateSelectedDateView(currentSelectedDate)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.updating()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}