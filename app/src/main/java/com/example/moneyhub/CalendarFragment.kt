package com.example.moneyhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.adapter.TransactionRecyclerAdapter
import com.example.moneyhub.data.model.TransactionRecyclerDataClass
import com.example.moneyhub.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: TransactionRecyclerAdapter
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 캘린더 샘플 데이터
    private val calendarData = listOf(
        TransactionRecyclerDataClass(
            date = "2024-11-01",
            icon = R.drawable.icon_food_category,
            title = "간식 사업 지출",
            category = "학생 복지",
            transaction = -120000.0
        ),
        TransactionRecyclerDataClass(
            date = "2024-11-04",
            icon = R.drawable.icon_food_category,
            title = "희진이 간식비",
            category = "희진이 복지",
            transaction = -7700.0
        ),
        TransactionRecyclerDataClass(
            date = "2024-11-08",
            icon = R.drawable.icon_food_category,
            title = "지환이 노래방",
            category = "지환이 복지",
            transaction = -10000.0
        ),
        TransactionRecyclerDataClass(
            date = "2024-11-10",
            icon = R.drawable.icon_food_category,
            title = "정기 회비",
            category = "수입",
            transaction = 100000.0
        )
    )

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
        updateMonthlyTotals()
    }

    private fun setupRecyclerView() {
        adapter = TransactionRecyclerAdapter(calendarData, true)
        binding.transactionList.apply {
            adapter = this@CalendarFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCalendarView() {
        // 각 날짜별 거래 금액 계산
        val dailyTotals = mutableMapOf<String, Double>()
        calendarData.forEach { transaction ->
            dailyTotals[transaction.date] = (dailyTotals[transaction.date] ?: 0.0) + transaction.transaction
        }

        // 날짜 선택 리스너 설정
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // 선택된 날짜의 거래 내역 필터링 및 RecyclerView 업데이트
            val transactionsForDate = calendarData.filter { it.date == selectedDate }
            adapter = TransactionRecyclerAdapter(transactionsForDate, true)
            binding.transactionList.adapter = adapter

            // 선택된 날짜의 총액을 표시할 TextView 업데이트
            val totalForDate = dailyTotals[selectedDate] ?: 0.0
            binding.selectedDateAmount.text = if (totalForDate != 0.0) {
                String.format("%.0f원", totalForDate)
            } else ""
        }
    }

    private fun updateMonthlyTotals() {
        var income = 0.0
        var expense = 0.0

        calendarData.forEach { transaction ->
            if (transaction.transaction > 0) {
                income += transaction.transaction
            } else {
                expense += -transaction.transaction
            }
        }

        // Income과 Expense TextView 업데이트
        binding.textViewIncome.text = String.format("$ %.0f", income)
        binding.textViewExpense.text = String.format("$ %.0f", expense)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}