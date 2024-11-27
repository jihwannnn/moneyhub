package com.example.moneyhub.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityRegisterDetailsBinding

class RegisterDetailsActivity : AppCompatActivity() {
    // ViewBinding 객체를 담을 변수
    private lateinit var binding: ActivityRegisterDetailsBinding

    // 카테고리 목록을 Spinner에 표시하기 위한 어댑터
    private lateinit var categoryAdapter: ArrayAdapter<String>

    // 카테고리 목록을 담는 변경 가능한 리스트
    // mutableListOf를 사용하여 나중에 새로운 카테고리를 추가할 수 있음
    private val categories = mutableListOf("선택해주세요", "회식", "지원금", "복지사업", "관리")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityRegisterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 처리
        getTransactionData()

        // UI 요소들 설정
        setupView()
        setupCategorySpinner()
    }

    // Intent로부터 데이터를 받아서 화면에 설정하는 함수
    private fun getTransactionData() {
        // Intent에서 데이터 가져오기
        val date = intent.getStringExtra("date")
        val title = intent.getStringExtra("title")
        val category = intent.getStringExtra("category")
        val amount = intent.getDoubleExtra("amount", 0.0)

        // 가져온 데이터를 EditText에 설정
        binding.apply {
            detailTitle.setText(title)
            detailAmount.setText(amount.toString())

            // 카테고리에서 | 기호 이전의 실제 카테고리명만 추출
            val cleanCategory = category?.split("|")?.get(0)?.trim()
            // 해당하는 카테고리의 위치를 찾아 스피너에서 선택
            val categoryPosition = categories.indexOfFirst { it == cleanCategory }.takeIf { it != -1 } ?: 0
            categorySpinner.setSelection(categoryPosition)
        }
    }

    private fun setupView() {
        // 등록 버튼 설정
        binding.btnRegister.setOnClickListener {
            // 등록 처리
            finish()
        }

        // 카테고리 추가 버튼에 클릭 리스너 설정
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()  // 카테고리 추가 다이얼로그 표시
        }
    }

    private fun setupCategorySpinner() {
        // ArrayAdapter 생성
        // 파라미터: Context, 각 항목의 레이아웃, 데이터 목록
        categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            // 드롭다운될 때 보여질 레이아웃 설정
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Spinner에 어댑터 설정
        binding.categorySpinner.adapter = categoryAdapter

        // Spinner의 아이템 선택 리스너 설정
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 아이템이 선택되었을 때 호출
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {  // "선택해주세요"가 아닌 경우
                    val selectedCategory = categories[position]
                    // TODO: 선택된 카테고리에 대한 추가 처리
                }
            }

            // 아무것도 선택되지 않았을 때 호출
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 필요한 경우 여기에 처리 추가
            }
        }
    }

    private fun showAddCategoryDialog() {
        // 다이얼로그에서 사용할 EditText 생성 및 설정
        val editText = EditText(this).apply {
            hint = "새로운 카테고리 입력"
            setPadding(50, 30, 50, 30)  // 여백 설정
        }

        // AlertDialog 생성 및 설정
        AlertDialog.Builder(this)
            .setTitle("카테고리 추가")  // 다이얼로그 제목
            .setView(editText)          // EditText를 다이얼로그에 추가
            .setPositiveButton("추가") { dialog, _ ->
                // 입력된 텍스트 가져오기 (앞뒤 공백 제거)
                val newCategory = editText.text.toString().trim()
                // 카테고리가 비어있지 않고 중복되지 않는 경우에만 추가
                if (newCategory.isNotEmpty() && !categories.contains(newCategory)) {
                    categories.add(newCategory)  // 새 카테고리 추가
                    categoryAdapter.notifyDataSetChanged()  // 어댑터에 변경 알림
                    // 새로 추가된 카테고리 선택
                    binding.categorySpinner.setSelection(categories.size - 1)
                }
                dialog.dismiss()  // 다이얼로그 닫기
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()  // 다이얼로그 닫기
            }
            .show()  // 다이얼로그 표시
    }

    // 안드로이드 기본 뒤로가기 버튼 처리
    override fun onBackPressed() {
        super.onBackPressed()
        finish()  // 액티비티 종료
    }
}