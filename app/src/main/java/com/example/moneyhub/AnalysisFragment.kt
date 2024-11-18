package com.example.moneyhub

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.example.moneyhub.databinding.FragmentAnalysisBinding

// 일별 지출 관련 import
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AnalysisFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAnalysisBinding

    // 카테고리별 색상을 저장하는 Map
    private val categoryColors = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
        setupLineChart()


        // 테스트용 샘플 데이터 (실제 데이터로 교체 필요)
        val sampleCategories = listOf(
            Category("주거비", 450000),
            Category("식비", 380000),
            Category("교통비", 150000),
            Category("문화생활", 120000),
            Category("쇼핑", 100000),
            Category("의료비", 80000),
            Category("교육비", 70000),
            Category("기타지출", 50000)
        )

        // 샘플데이터로 차트 표시
        loadPieChartData(sampleCategories)
        loadLineChartData(sampleDailyExpenses)

    }

    // 파이 차트의 기본 설정을 정의하는 함수
    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true  // 도넛 모양으로 만들기 위해 가운데 구멍을 활성화
            setHoleColor(Color.WHITE) // 가운데 구멍의 색상을 흰색으로 설정
            transparentCircleRadius = 60f  // 투명한 원의 반지름 설정
            holeRadius = 58f              // 가운데 구멍의 반지름 설정
            setUsePercentValues(true)     // 값을 퍼센트로 표시
            description.isEnabled = false  // 차트 설명 비활성화

            // 범례 설정
            legend.apply {
                isEnabled = true                  // 범례 활성화
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER  // 가운데 정렬
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM     // 아래쪽 배치
                orientation = Legend.LegendOrientation.HORIZONTAL             // 가로 방향 배치
                setDrawInside(false)             // 차트 외부에 범례 표시
                xEntrySpace = 10f                // 범례 항목 간 가로 간격
                textSize = 12f                   // 범례 텍스트 크기
            }

            setEntryLabelColor(Color.BLACK) // 라벨 색상 설정
            setEntryLabelTextSize(12f)      // 라벨 텍스트 크기 설정
            animateY(1000)                  // Y축 기준 1초 동안 애니메이션 효과
        }
    }




    // 라인 차트 기본 설정
//    private fun setupLineChart() {
//        binding.lineChart.apply {
//            description.isEnabled = false
//            setTouchEnabled(true)
//            setDrawGridBackground(false)
//            xAxis.setDrawGridLines(false)
//            axisRight.isEnabled = false
//            animateX(1000)
//        }
//    }

    private fun setupLineChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // x축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // 최소 간격을 1로 설정
                labelCount = 31  // 1-31일 표시
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}일" // x축에 '일' 표시 추가
                    }
                }
            }

            // y축 설정
            axisLeft.apply {
                setDrawGridLines(true)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}만"  // y축에 '만' 표시 추가
                    }
                }
            }

            axisRight.isEnabled = false

            // 차트 크기 조정
            layoutParams = layoutParams.apply {
                width = resources.displayMetrics.widthPixels * 2 // 화면 너비의 2배
            }

            animateX(1000)
        }
    }



    // 차트에 데이터를 로드하고 표시하는 함수
    private fun loadPieChartData(categories: List<Category>) {
        val entries = ArrayList<PieEntry>()

        // 앱의 테마와 어울리는 색상 정의
        val predefinedColors = listOf(
            Color.parseColor("#2DD4BF"),  // 메인 에메랄드
            Color.parseColor("#FEF08A"),  // 레몬 옐로우
            Color.parseColor("#7DD3FC"),  // 하늘색
            Color.parseColor("#C4B5FD"),  // 라벤더
            Color.parseColor("#FDA4AF"),  // 연한 핑크
        )

        // 기타 항목을 위한 회색 정의
        val etcColor = Color.parseColor("#94A3B8")

        // 금액이 큰 순서대로 정렬하고 상위 5개만 선택
        val topCategories = categories
            .sortedByDescending { it.amount }
            .take(5)

        // 상위 5개를 제외한 나머지 카테고리들의 총액 계산
        val otherAmount = categories
            .sortedByDescending { it.amount }
            .drop(5)  // 상위 5개를 제외한 나머지
            .sumOf { it.amount }  // 나머지의 총합 계산

        // 전체 지출 금액 계산 (퍼센트 계산에 사용)
        val totalAmount = categories.sumOf { it.amount }.toFloat()

        // 상위 5개 카테고리를 차트에 추가
        topCategories.forEachIndexed { index, category ->
            val percentage = (category.amount.toFloat() / totalAmount) * 100
            entries.add(PieEntry(percentage, category.name))
        }

        // 나머지 카테고리들을 '기타'로 묶어서 추가
        if (otherAmount > 0) {
            val otherPercentage = (otherAmount.toFloat() / totalAmount) * 100
            entries.add(PieEntry(otherPercentage, "기타"))
        }

        // 기타 항목이 있으면 회색을 추가, 없으면 entries 크기만큼만 색상 사용
        val colors = if (otherAmount > 0) {
            predefinedColors + etcColor
        } else {
            predefinedColors.take(entries.size)
        }

        // 차트 데이터셋 설정
        val dataSet = PieDataSet(entries, "월별 지출").apply {
            this.colors = colors           // 색상 설정
            valueTextColor = Color.BLACK   // 값 텍스트 색상
            valueTextSize = 12f           // 값 텍스트 크기
            sliceSpace = 3f              // 조각 사이의 간격
        }

        // 차트 데이터 설정
        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))  // 퍼센트 형식으로 값 표시
            setValueTextSize(12f)
            setValueTextColor(Color.BLACK)
        }

        // 차트에 데이터 적용
        binding.pieChart.data = data
        binding.pieChart.invalidate()  // 차트 새로고침
    }

    // 카테고리 데이터를 담는 클래스
    data class Category(
        val name: String,   // 카테고리 이름
        val amount: Int     // 지출 금액
    )

    // 데이터 로드 함수 수정
    private fun loadLineChartData(dailyExpenses: List<DailyExpense>) {
        val entries = dailyExpenses.map { expense ->
            Entry(expense.day.toFloat(), expense.amount / 10000f)  // 금액을 만 단위로 변환
        }

        val dataSet = LineDataSet(entries, "일별 지출").apply {
            color = Color.parseColor("#2DD4BF")  // 에메랄드 색상
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.parseColor("#2DD4BF"))
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(true)
            valueTextSize = 10f
        }

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }

    data class DailyExpense(
        val day: Int,     // 일자 (1-31)
        val amount: Int   // 지출 금액
    )

    // 샘플 데이터
    private val sampleDailyExpenses = listOf(
        DailyExpense(1, 50000),  // 1일차 지출
        DailyExpense(3, 30000),  // 3일차 지출
        DailyExpense(7, 45000),  // 7일차 지출
        DailyExpense(10, 25000), // 10일차 지출
        DailyExpense(13, 60000)  // 13일차 지출
    )


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}