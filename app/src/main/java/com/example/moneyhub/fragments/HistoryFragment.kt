package com.example.moneyhub.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.activity.RegisterDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentHistoryBinding
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.utils.DateUtils
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint  // Hilt 의존성 주입을 위한 어노테이션
class HistoryFragment : Fragment() {
    // Fragment 수준에서 공유되는 ViewModel 참조
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    // ViewBinding null 안전성을 위한 처리
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!  // !! 연산자로 null이 아님을 보장

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: TransactionAdapter

    // RegisterDetailsActivity로부터의 결과를 처리하는 launcher
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.let { intent ->
                // 새로운 거래내역 객체 생성
                val newTransaction = Transaction(
                    tid = System.currentTimeMillis().toString(),
                    title = intent.getStringExtra("title") ?: "",
                    category = "${intent.getStringExtra("category")} |",
                    type = intent.getBooleanExtra("type", false),
                    amount = intent.getLongExtra("amount", 0L),
                    content = intent.getStringExtra("content") ?: "",
                    payDate = intent.getLongExtra("payDate", System.currentTimeMillis()),
                    verified = true,
                    createdAt = System.currentTimeMillis()
                )
                // 생성된 거래내역을 SharedViewModel에 추가
                sharedViewModel.addTransaction(newTransaction)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
            val intent = Intent(requireActivity(), RegisterDetailsActivity::class.java)
            startForResult.launch(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG: HistoryFragment onViewCreated")

        setupRecyclerView()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                println("DEBUG: Starting to collect filtered histories")
                sharedViewModel.filteredHistories.collect { transactions ->
                    println("DEBUG: Received filtered update, size = ${transactions.size}")
                    if (transactions.isNotEmpty()) {
                        println("DEBUG: Transaction dates = ${
                            transactions.map { DateUtils.millisToDate(it.payDate) }
                        }")
                    }
                    adapter.updateData(transactions)
                }
            }
        }
    }

    // RecyclerView 설정을 위한 함수
    private fun setupRecyclerView() {
        // 어댑터를 빈 리스트로 초기화 (데이터는 나중에 observe에서 업데이트)
        adapter = TransactionAdapter(emptyList(), false)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }



    // 메모리 누수 방지를 위한 binding null 처리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}