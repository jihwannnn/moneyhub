package com.example.moneyhub.activity

// 현재 시스템의 날짜와 시간을 가져오는 클래스 --> 자동으로 날짜를 입력해 거래 내역 생성을 하기 위해
import java.util.Date

// 지역 설정을 처리하는 클래스, 날짜 형식이나 숫자 표기 등의 지역화에 사용한다.
import java.util.Locale

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityRegisterDetailsBinding
import com.example.moneyhub.model.Transaction
import java.util.Calendar

class RegisterDetailsActivity : AppCompatActivity() {
    // ViewBinding 객체를 담을 변수
    private lateinit var binding: ActivityRegisterDetailsBinding

    // 카테고리 목록을 Spinner에 표시하기 위한 어댑터
    private lateinit var categoryAdapter: ArrayAdapter<String>

    // 카테고리 목록을 담는 변경 가능한 리스트
    // mutableListOf를 사용하여 나중에 새로운 카테고리를 추가할 수 있음
    private val categories = mutableListOf("선택해주세요", "회식", "지원금", "복지사업", "관리")


    // 현재 선택된 거래 유형을 저장할 변수
    private var isIncome = false // 기본값은 지출으로 설정


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
        setupTransactionTypeRadioGroup()
        setupDatePicker()

        // 초기 라디오 버튼 상태 설정 - 지출이 기본 선택되도록
        binding.radioExpense.isChecked = true
    }

    // Intent로부터 데이터를 받아서 화면에 설정하는 함수
    private fun getTransactionData() {
        // Intent에서 데이터 가져오기
//        val date = intent.getStringExtra("date")

        val title = intent.getStringExtra("title")
        val category = intent.getStringExtra("category")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val type = intent.getBooleanExtra("type", true)

        // 가져온 데이터를 EditText에 설정
        binding.apply {
            detailTitle.setText(title)
            detailAmount.setText(amount.toString())

            // 수입/지출 라디오 버튼 설정
            if (type) {
                radioIncome.isChecked = true
            } else {
                radioExpense.isChecked = true
            }


            // 카테고리에서 | 기호 이전의 실제 카테고리명만 추출
            val cleanCategory = category?.split("|")?.get(0)?.trim()
            // 해당하는 카테고리의 위치를 찾아 스피너에서 선택
            val categoryPosition = categories.indexOfFirst { it == cleanCategory }.takeIf { it != -1 } ?: 0
            categorySpinner.setSelection(categoryPosition)
        }
    }

    //라디오 버튼의 선택이 변경될 때마다 자동으로 호출되는 함수
    // 즉, 사용자가 수입/지출 라디오 버튼을 클릭할 때마다 이 코드가 실행되어 금액의 부호를 자동으로 조정한다.
    private fun setupTransactionTypeRadioGroup(){

        // _ --> 사용하지 않는 매개변수를 나타내는 코틀린 표기법
        // 첫번째 매개변수는 transactionTypeGroup인데, 이 변수를 사용하지 않을 것이므로 _로 표시한다.
        binding.transactionTypeGroup.setOnCheckedChangeListener { _, checkedId ->

            // radioIncome의 id를 checkId에 저장하고 isIncome, 즉 수입으로 설정이 되어 있는지 true or false로 저장되어 있음
            // radioIncome 버튼을 선택하면 R.id.radioIncome이 checkId로 전달된다.
            // checkedId가 radioIncome의 ID와 같으면 true (수입)
            // checkedId가 radioIncome의 ID와 다르면 false (지출)를 isIncome 변수에 저장

            when (checkedId) {
                R.id.radioIncome -> isIncome = true   // 수입 선택됨
                R.id.radioExpense -> isIncome = false // 지출 선택됨
            }
        }
        // 금액이 입력되어 있을 때 부호 자동 변경
        // 현재 금액을 detailAmount에 적힌 double 또는 null이 가능한 텍스트를 string으로 변환해서 저장한다.
        val currentAmount = binding.detailAmount.text.toString().toDoubleOrNull()

        //currentAmount가 null이 아니면 해당 값을 절댓값으로 바꿔서 absAmount에 넣는다
        currentAmount?.let{ amount ->
            val absAmount = kotlin.math.abs(amount)

            // 만약 income으로 체크가 되어 있지 않고 amount가 0이 아니면 --> 지출일 경우
            if(!isIncome && amount > 0){

                // - 표시를 넣는다
                binding.detailAmount.setText((-absAmount).toString())
            }
            // 양수일 경우
            else if (isIncome && amount < 0){
                // 그대로 절댓값 상태로 둔다.
                binding.detailAmount.setText(absAmount.toString())
            }
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

        // 거래 내역 상세 등록 페이지에서 register 버튼을 누르면, 자동으로 등록되도록 클릭 리스너 설정
        binding.btnRegister.setOnClickListener {
            // 유저가 입력한 값을 가져온다
            // TransactionItem Data Class에 맞게
            val title = binding.detailTitle.text.toString()
            var amount = binding.detailAmount.text.toString().toLongOrNull() ?: 0
            val category = binding.categorySpinner.selectedItem.toString()
            val content = binding.detailMemo.text.toString()

            // 현재 시스템의 날짜를 "yyyy-MM-dd" 형식으로 자동 입력
            // val currentDate =
            // java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 금액의 부호 처리
            if (!isIncome) {
                amount = -kotlin.math.abs(amount)
            }


            //Transaction객체 생성
            val newTransaction = Transaction(
                // 현재 시간을 id로 사용
                tid = System.currentTimeMillis().toString(), // String으로 변환
                title = title,
                category = category,
                type = isIncome,
                amount = amount,
                content = content,
                payDate = System.currentTimeMillis(),
                verified = true, // 실제 내역이므로 true
                createdAt = System.currentTimeMillis()
            )

            // 데이터를 Intend에 담아서 BudgetFragement로 전달
            val intent = Intent().apply {
                putExtra("title", newTransaction.title)
                putExtra("amount", newTransaction.amount)
                putExtra("category", newTransaction.category)
                putExtra("type", newTransaction.type)
                putExtra("content", newTransaction.content)
                putExtra("payDate", newTransaction.payDate)
            }

            // 이전 액티비티인 BudgetFragement로 데이터를 전달하는 역할을 한다.
            // Result.OK --> 작업이 성공적으로 완료되었음을 알림
            // intent: 전달할 데이터를 담은 객체
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }




    private fun setupDatePicker() {
        // 캘린더 객체 생성 - 현재 날짜/시간 정보를 가지고 있음
        val calendar = Calendar.getInstance()

        // 날짜 선택 시 동작할 리스너 정의
        // _, year, month, day는 사용자가 DatePicker에서 선택한 날짜 값이 전달됨
        val datePickerDialog = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // 선택된 날짜로 캘린더 객체 업데이트
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            // 날짜 포맷 지정 (yyyy-MM-dd)
            val myFormat = "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
            // 포맷에 맞춰 날짜를 문자열로 변환하여 EditText에 표시
            binding.dateSelectEdit.setText(dateFormat.format(calendar.time))
        }

        // EditText 클릭 시 DatePickerDialog 표시
        binding.dateSelectEdit.setOnClickListener {
            DatePickerDialog(
                this,  // 현재 액티비티 컨텍스트
                datePickerDialog,  // 위에서 정의한 날짜 선택 리스너
                calendar.get(Calendar.YEAR),  // 현재 년도
                calendar.get(Calendar.MONTH),  // 현재 월
                calendar.get(Calendar.DAY_OF_MONTH)  // 현재 일
            ).apply {
                // DatePicker의 선택 가능한 날짜 범위 설정
                datePicker.minDate = Calendar.getInstance().timeInMillis  // 최소 날짜: 오늘
                calendar.add(Calendar.YEAR, 20)  // 캘린더에 20년 추가
                datePicker.maxDate = calendar.timeInMillis  // 최대 날짜: 20년 후
            }.show()  // 다이얼로그 표시
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