package com.example.moneyhub.activity.viewtransactiondetails

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.databinding.ActivityViewTransactionDetailsBinding
import com.example.moneyhub.model.sessions.TransactionSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ViewTransactionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTransactionDetailsBinding
    private val viewModel: ViewTransactionDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // 닫기 버튼 설정
        binding.btnClose.setOnClickListener {
            finish()
        }

        // TransactionSession에서 현재 Transaction 가져오기
        val transaction = TransactionSession.getCurrentTransaction()
        viewModel.loadTransaction(transaction)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.transaction.collect { transaction ->
                with(binding) {
                    // 제목
                    detailTitle.text = transaction.title

                    // 거래 유형 (수입/지출)
                    transactionType.text = if (transaction.type) "수입" else "지출"

                    // 금액 (₩ 기호와 천단위 구분자 포함)
                    val amountText = if (transaction.type) {
                        String.format("₩%,d", transaction.amount)
                    } else {
                        String.format("-₩%,d", transaction.amount)
                    }
                    detailAmount.text = amountText

                    // 날짜
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateSelectText.text = dateFormat.format(Date(transaction.payDate))

                    // 카테고리
                    categoryText.text = transaction.category

                    // 메모
                    detailMemo.text = transaction.content.ifEmpty { "메모 없음" }
                }
            }
        }
    }
}