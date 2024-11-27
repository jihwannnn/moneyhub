package com.example.moneyhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.model.TransactionItem
import com.example.moneyhub.databinding.FragmentCalendarBinding
import com.google.firebase.firestore.FirebaseFirestore

class CalendarFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: TransactionAdapter

    // 캘린더 샘플 데이터
    private val calendarData = listOf(
        TransactionItem(
            date = "2024-11-01",
            icon = R.drawable.icon_food_category,
            title = "간식 사업 지출",
            category = "학생 복지",
            transaction = -120000.0
        ),

        TransactionItem(
            date = "2024-11-01",
            icon = R.drawable.icon_food_category,
            title = "학생 회비",
            category = "회비 납부",
            transaction = 220000.0
        ),

        TransactionItem(
            date = "2024-11-04",
            icon = R.drawable.icon_food_category,
            title = "희진이 간식비",
            category = "희진이 복지",
            transaction = -7700.0
        ),
        TransactionItem(
            date = "2024-11-08",
            icon = R.drawable.icon_food_category,
            title = "지환이 노래방",
            category = "지환이 복지",
            transaction = -10000.0
        ),

        TransactionItem(
            date = "2024-11-10",
            icon = R.drawable.icon_food_category,
            title = "정기 회비",
            category = "수입",
            transaction = 100000.0
        ),
        TransactionItem(
            date = "2024-11-18",
            icon = R.drawable.icon_food_category,
            title = "지환이 노래방",
            category = "지환이 복지",
            transaction = -10000.0
        ),
        TransactionItem(
            date = "2024-11-21",
            icon = R.drawable.icon_food_category,
            title = "지환이 노래방",
            category = "지환이 복지",
            transaction = -10000.0
        ),

        TransactionItem(
            date = "2024-11-28",
            icon = R.drawable.icon_food_category,
            title = "지환이 노래방",
            category = "지환이 복지",
            transaction = -10000.0
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

    // RecyclerView 초기화 및 설정
    private fun setupRecyclerView() {
        adapter = TransactionAdapter(calendarData, true, true)
        binding.transactionList.apply {
            adapter = this@CalendarFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCalendarView() {
        val dailyTotals = mutableMapOf<String, Pair<Double, Double>>() // 날짜별 <수입, 지출> 맵

        // 각 날짜의 수입/지출 총액 계산
        calendarData.forEach { transaction ->
            val currentPair = dailyTotals[transaction.date] ?: Pair(0.0, 0.0)
            dailyTotals[transaction.date] = if (transaction.transaction > 0) {
                Pair(currentPair.first + transaction.transaction, currentPair.second)
            } else {
                Pair(currentPair.first, currentPair.second - transaction.transaction)
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // 선택된 날짜의 거래 내역 필터링
            val transactionsForDate = calendarData.filter { it.date == selectedDate }
            adapter = TransactionAdapter(transactionsForDate, true, true)
            binding.transactionList.adapter = adapter

            // 선택된 날짜의 수입/지출 표시
            val totals = dailyTotals[selectedDate] ?: Pair(0.0, 0.0)
            binding.selectedDateIncome.text = if (totals.first > 0) "+${totals.first.toInt()}" else ""
            binding.selectedDateExpense.text = if (totals.second > 0) "-${totals.second.toInt()}" else ""
        }
    }

    private fun updateMonthlyTotals() {
        var monthlyIncome = 0.0
        var monthlyExpense = 0.0

        calendarData.forEach { transaction ->
            if (transaction.transaction > 0) {
                monthlyIncome += transaction.transaction
            } else {
                monthlyExpense += -transaction.transaction
            }
        }

        binding.textViewIncome.text = String.format("$ %.0f", monthlyIncome)
        binding.textViewExpense.text = String.format("$ %.0f", monthlyExpense)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}