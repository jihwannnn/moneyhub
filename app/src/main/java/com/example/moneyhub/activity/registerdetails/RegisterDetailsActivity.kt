package com.example.moneyhub.activity.registerdetails

// 지역 설정을 처리하는 클래스, 날짜 형식이나 숫자 표기 등의 지역화에 사용한다.
import java.util.Locale

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.databinding.ActivityRegisterDetailsBinding
import com.example.moneyhub.model.sessions.RegisterTransactionSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class RegisterDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterDetailsBinding
    private val viewModel: RegisterDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        setupDatePicker()
        setupRegisterButton()
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dateSelectEdit.setText(sdf.format(calendar.time))
            binding.dateSelectEdit.tag = calendar.timeInMillis
        }

        binding.dateSelectEdit.setOnClickListener {
            DatePickerDialog(
                this,
                datePickerDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }



    private fun setupCategorySpinner() {
        // 카테고리 어댑터 초기화
        var categories = viewModel.category.value.category.toMutableList()
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // 스피너에 어댑터 설정
        binding.categorySpinner.adapter = categoryAdapter

        // 카테고리 리스트 업데이트 관찰
        lifecycleScope.launch {
            viewModel.category.collect { category ->
                categories = category.category.toMutableList()
                categoryAdapter.clear()
                categoryAdapter.addAll(categories)
                categoryAdapter.notifyDataSetChanged()
            }
        }

        // 현재 트랜잭션의 카테고리가 있다면 해당 포지션으로 설정
        lifecycleScope.launch {
            viewModel.currentTransaction.collect { transaction ->
                if (transaction.category.isNotEmpty()) {
                    val position = categories.indexOf(transaction.category)
                    if (position != -1) {
                        binding.categorySpinner.setSelection(position)
                    }
                }
            }
        }

        // 스피너 아이템 선택 리스너
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // 필요한 경우 선택된 카테고리 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때 처리
            }
        }

        // 카테고리 추가 버튼 설정
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog(categories)
        }
    }

    private fun showAddCategoryDialog(categories: MutableList<String>) {
        val editText = EditText(this).apply {
            hint = "새로운 카테고리 입력"
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("카테고리 추가")
            .setView(editText)
            .setPositiveButton("추가") { dialog, _ ->
                val newCategory = editText.text.toString().trim()
                if (newCategory.isNotEmpty() && !categories.contains(newCategory)) {
                    // ViewModel을 통해 카테고리 저장
                    viewModel.saveCategory(newCategory)
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val title = binding.detailTitle.text.toString()
            val amount = binding.detailAmount.text.toString().toLongOrNull() ?: 0L
            val category = binding.categorySpinner.selectedItem.toString()
            val content = binding.detailMemo.text.toString()
            val isIncome = binding.radioIncome.isChecked
            val payDate = binding.dateSelectEdit.tag as? Long ?: System.currentTimeMillis()

            viewModel.saveTransaction(
                title = title,
                category = category,
                type = isIncome,
                amount = amount,
                content = content,
                payDate = payDate
            )
        }
    }

    private fun observeViewModel() {
        setupCategorySpinner()

        // Load initial transaction data
        lifecycleScope.launch {
            viewModel.currentTransaction.collect { transaction ->
                binding.apply {
                    detailTitle.setText(transaction.title)
                    detailAmount.setText(transaction.amount.toString())
                    detailMemo.setText(transaction.content)

                    // Set date
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateSelectEdit.setText(sdf.format(transaction.payDate))
                    dateSelectEdit.tag = transaction.payDate

                    // Set transaction type
                    radioIncome.isChecked = transaction.type
                    radioExpense.isChecked = !transaction.type
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        // Show loading if needed
                    }
                    state.isSuccess -> {
                        Toast.makeText(this@RegisterDetailsActivity,
                            "거래가 저장되었습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    state.error != null -> {
                        Toast.makeText(this@RegisterDetailsActivity,
                            state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 안드로이드 기본 뒤로가기 버튼 처리
    override fun onBackPressed() {
        super.onBackPressed()
        RegisterTransactionSession.clearCurrentTransaction()
        finish()  // 액티비티 종료
    }
}