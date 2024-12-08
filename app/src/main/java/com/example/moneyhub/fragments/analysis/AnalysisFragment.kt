package com.example.moneyhub.fragments.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.databinding.FragmentAnalysisBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalysisViewModel by viewModels()

    // 차트 색상 정의
    private val chartColors = listOf(
        Color.parseColor("#2DD4BF"),  // 메인 에메랄드
        Color.parseColor("#FEF08A"),  // 레몬 옐로우
        Color.parseColor("#7DD3FC"),  // 하늘색
        Color.parseColor("#C4B5FD"),  // 라벤더
        Color.parseColor("#FDA4AF"),  // 연한 핑크
        Color.parseColor("#94A3B8")   // 회색 (기타)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonthNavigation()
        setupCharts()
        observeViewModel()
    }

    private fun setupMonthNavigation() {
        binding.apply {
            imageViewPreviousMonthButton.setOnClickListener {
                viewModel.moveToPreviousMonth()
            }

            imageViewNextMonthButton.setOnClickListener {
                viewModel.moveToNextMonth()
            }

            // 현재 선택된 월 표시 관찰
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.currentYearMonth.collect {
                    currentMonthText.text = viewModel.getMonthDisplayText()
                }
            }
        }
    }

    private fun setupCharts() {
        setupPieChart()
        setupLineChart()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "지출 분석"
            setCenterTextSize(16f)
            setCenterTextColor(Color.BLACK)

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textSize = 12f
                textColor = Color.BLACK
                xEntrySpace = 10f
                yEntrySpace = 0f
            }
        }
    }

    private fun setupLineChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                textSize = 12f
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${value.toInt()}일"
                }
            }

            axisLeft.apply {
                textColor = Color.BLACK
                textSize = 12f
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "₩${(value/10000).toInt()}만"
                }
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                textColor = Color.BLACK
                textSize = 12f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                form = Legend.LegendForm.CIRCLE
            }

            animateX(1000)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryData.collect { categories ->
                updatePieChart(categories)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyData.collect { dailyAmounts ->
                updateLineChart(dailyAmounts)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalIncome.collect { income ->
                binding.textViewIncome.text = String.format("₩ %,d", income)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalExpense.collect { expense ->
                binding.textViewExpense.text = String.format("₩ %,d", expense)
            }
        }
    }

    private fun updatePieChart(categories: List<AnalysisViewModel.CategoryAmount>) {
        val entries = categories.map { category ->
            PieEntry(category.percentage, category.name)
        }

        val dataSet = PieDataSet(entries, null).apply {
            colors = chartColors.take(entries.size)
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            valueFormatter = PercentFormatter(binding.pieChart)
            selectionShift = 5f
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            highlightValues(null)
            invalidate()
        }
    }

    private fun updateLineChart(dailyAmounts: List<AnalysisViewModel.DailyAmount>) {
        val entries = dailyAmounts.map { daily ->
            Entry(daily.day.toFloat(), daily.amount.toFloat())
        }

        val dataSet = LineDataSet(entries, "일별 수입/지출").apply {
            color = Color.parseColor("#2DD4BF")
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#2DD4BF"))
            circleRadius = 4f
            setDrawCircleHole(true)
            circleHoleRadius = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawFilled(true)
            fillColor = Color.parseColor("#2DD4BF")
            fillAlpha = 50
        }

        binding.lineChart.apply {
            data = LineData(dataSet)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}