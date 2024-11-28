package com.example.moneyhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.activity.CameraActivity
import com.example.moneyhub.activity.RegisterDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentBudgetBinding
import com.example.moneyhub.model.TransactionItem

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BudgetFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBudgetBinding
    private lateinit var recyclerViewAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBudgets()
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
        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        initRecyclerView()

        // 거래 내역 + 버튼 리스너 추가
        binding.btnAddBudget.setOnClickListener{
            val intent = Intent(requireActivity(), RegisterDetailsActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun initRecyclerView() {
        val budgetData = mutableListOf(
            TransactionItem(0, "2024-11-01", R.drawable.icon_food_category,
                "간식 사업 지출 (예정)", "학생 복지 |", -120000.0),
            TransactionItem(1, "2024-11-02", R.drawable.icon_food_category,
                "희진이 간식비 (예정)", "희진이 복지 |", -7700.0),
            TransactionItem(2, "2024-11-03", R.drawable.icon_food_category,
                "지환이 지각비 (예정)", "지환이 복지 |", 10000.0),
            TransactionItem(3, "2024-11-03", R.drawable.icon_food_category,
                "그 외 Title (예정)", "그 외 category |", -1000.0)
        )

        recyclerViewAdapter = TransactionAdapter(budgetData, isForBudget = true) { transactionItem ->
            val intent = Intent(requireContext(), CameraActivity::class.java).apply {
                putExtra("transaction_id", transactionItem.id) //id 전달
                putExtra("transaction_date", transactionItem.date) //id 전달
                putExtra("transaction_title", transactionItem.title) //id 전달
                putExtra("transaction_category", transactionItem.category) //id 전달
                putExtra("transaction_amount", transactionItem.amount) //id 전달

            }
            startActivity(intent)
        }

        binding.recyclerViewBudget.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }
    }

    private fun loadBudgets() {
        // TODO: Firestore에서 예산 데이터 가져오기
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BudgetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}