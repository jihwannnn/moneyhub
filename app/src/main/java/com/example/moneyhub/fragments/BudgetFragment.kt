package com.example.moneyhub.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.activity.camera.CameraActivity
import com.example.moneyhub.activity.registerdetails.RegisterDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentBudgetBinding
import com.example.moneyhub.model.Transaction

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BudgetFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBudgetBinding
    private lateinit var recyclerViewAdapter: TransactionAdapter

    private val budgetData = mutableListOf<Transaction>()  // 추가


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
            // 다른 액티비티를 시작하고, 그 결과를 받아올 때 사용, intent: 실행할 액티비티 정보
            // ADD_BUDGET_TRANSACTION_REQUEST >> 요청을 구분하기 위한 코드
            startActivityForResult(intent, ADD_BUDGET_TRANSACTION_REQUEST)
        }


        return binding.root
    }


    private fun initRecyclerView() {
        // budgetData mutable list를 초기화하는 더미 데이터
        budgetData.addAll(listOf(
        Transaction(
            tid = "0",
            title = "간식 사업 지출 (예정)",
            category = "학생 복지 |",
            type = false,
            amount = 120000L,
            content = "",
            payDate = System.currentTimeMillis(),
            verified = false,
            createdAt = System.currentTimeMillis()
        ),
        Transaction(
            tid = "1",
            title = "희진이 간식비 (예정)",
            category = "희진이 복지 |",
            type = false,
            amount = 7700L,
            content = "",
            payDate = System.currentTimeMillis(),
            verified = false,
            createdAt = System.currentTimeMillis()
        ),
        Transaction(
            tid = "2",
            title = "지환이 지각비 (예정)",
            category = "지환이 복지 |",
            type = true,
            amount = 10000L,
            content = "",
            payDate = System.currentTimeMillis(),
            verified = false,
            createdAt = System.currentTimeMillis()
        )
        ))

        recyclerViewAdapter = TransactionAdapter(budgetData, isForBudget = true) { transaction ->
            val intent = Intent(requireContext(), CameraActivity::class.java).apply {
                putExtra("transaction_id", transaction.tid) // tid 전달
                putExtra("transaction_date", transaction.payDate)
                putExtra("transaction_title", transaction.title)
                putExtra("transaction_category", transaction.category)
                putExtra("transaction_amount", transaction.amount)

            }
            startActivity(intent)
        }

        binding.recyclerViewBudget.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }
    }

    // startActivityForResult로 실행된 Activity가 종료될 때 호출되는 콜백 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // requestCode: 예산 거래 내역 추가 요청이 맞는 지 확인
        // resultCode: 작업 결과 상태
        // data: 이전에 액티비티에서 전달받은 데이터

        //RegisterDetailsActivity에서 받은 데이터로 새 TransactionItem 생성하고,
        //budgetData에 추가하고 RecyclerView 갱신하는 코드
        if (requestCode == ADD_BUDGET_TRANSACTION_REQUEST && resultCode == Activity.RESULT_OK){
            data?.let { intent ->
                val newTransaction = Transaction(
                    tid = System.currentTimeMillis().toString(),
                    title = intent.getStringExtra("title") ?: "",
                    category = "${intent.getStringExtra("category")} |",
                    type = intent.getBooleanExtra("type", false),
                    amount = intent.getLongExtra("amount", 0L),
                    content = intent.getStringExtra("content") ?: "",
                    payDate = intent.getLongExtra("payDate", System.currentTimeMillis()),
                    verified = false,
                    createdAt = System.currentTimeMillis()
                )
                budgetData.add(newTransaction)
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun loadBudgets() {
        // TODO: Firestore에서 예산 데이터 가져오기
    }

    companion object {
        //100은 요청 코드(request code)로,
        // 여러 Activity 결과들을 구분하기 위한 고유 식별자.
        // onActivityResult에서 이 코드를 확인해 어떤 Activity에서 돌아온 결과인지 구분한다.
        //다른 값을 사용해도 되지만, 고유한 값이어야 하며 상수로 선언하는 것이 코드 관리에 좋다
        private const val ADD_BUDGET_TRANSACTION_REQUEST = 100  // 추가

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