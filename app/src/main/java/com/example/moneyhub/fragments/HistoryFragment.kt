package com.example.moneyhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.activity.registerdetails.RegisterDetailsActivity
import com.example.moneyhub.activity.viewtransactiondetails.ViewTransactionDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentHistoryBinding
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.model.sessions.RegisterTransactionSession
import com.example.moneyhub.model.sessions.TransactionSession
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // Hilt 의존성 주입을 위한 어노테이션
class HistoryFragment : Fragment() {
    // Fragment 수준에서 공유되는 ViewModel 참조
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    // ViewBinding null 안전성을 위한 처리
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!  // !! 연산자로 null이 아님을 보장

    private lateinit var adapter: TransactionAdapter
    val user = CurrentUserSession.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewBinding 초기화
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // RecyclerView 초기화
        setupRecyclerView()

        // 새 거래내역 추가 버튼 설정
        setupAddButton()

        return binding.root
    }

    // 거래내역 추가 버튼 설정
    private fun setupAddButton() {
        binding.btnAddHistory.setOnClickListener {
            RegisterTransactionSession.setTransaction(Transaction(verified = true))
            if(user.role != Role.REGULAR) {
                val intent = Intent(requireActivity(), RegisterDetailsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG: HistoryFragment onViewCreated")

        setupRecyclerView()
        observeViewModel()
    }



    // RecyclerView 설정을 위한 함수
    private fun setupRecyclerView() {
        // 어댑터를 빈 리스트로 초기화 (데이터는 나중에 observe에서 업데이트)
        adapter = TransactionAdapter(
            transactions = emptyList(),
            isForBudget = false,
            onItemClick = { transaction ->

                // 클릭된 Transaction을 TransactionSession에 저장
                TransactionSession.setTransaction(transaction)
                // ViewTransactionDetailsActivity 시작
                val intent = Intent(requireContext(), ViewTransactionDetailsActivity::class.java)
                startActivity(intent)
            },
            onDeleteClick = { transaction ->
                // 현재 유저가 REGULAR가 아닐 때만 삭제 가능
                if (user.role != Role.REGULAR) {
                    sharedViewModel.currentUser.value?.currentGid?.let { gid ->
                        sharedViewModel.deleteTransaction(gid, transaction.tid)
                    }
                }
            }
        )



        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun updateMonthlyTotals(transactions: List<Transaction>) {
        var monthlyIncome = 0L
        var monthlyExpense = 0L

        // 모든 거래내역을 순회하면서 내역의 수입과 지출 합계 계산
        transactions.forEach { transaction ->
            if (transaction.type) {  // type이 true면 수입
                monthlyIncome += transaction.amount
            } else {  // type이 false면 지출
                monthlyExpense += transaction.amount  // 지출은 음수로 저장되어 있으므로 양수로 변환
            }
        }

        // 계산된 총액을 TextView에 표시
        binding.textViewIncome.text = String.format(" ₩%,d", monthlyIncome)  // 천단위 구분자(,) 포함
        binding.textViewExpense.text = String.format("₩%,d", monthlyExpense)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.filteredHistories.collect { transactions ->
                    adapter.updateData(transactions)
                    updateMonthlyTotals(transactions)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentYearMonth.collect { yearMonth ->
                    sharedViewModel.updateMonthlyTransactions(yearMonth)
                }
            }
        }
    }

    // 메모리 누수 방지를 위한 binding null 처리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.updating()
    }
}