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
import com.example.moneyhub.adapter.TransactionRecyclerAdapter
import com.example.moneyhub.data.model.TransactionRecyclerDataClass
import com.example.moneyhub.databinding.FragmentBudgetBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BudgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BudgetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentBudgetBinding

    private lateinit var recyclerViewAdapter: TransactionRecyclerAdapter

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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(inflater, container, false)

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        // 더미 데이터 생성
        val budgetData = listOf(
            TransactionRecyclerDataClass(
                "2024-11-01",
                R.drawable.icon_food_category,
                "간식 사업 지출 (예정)",
                "학생 복지 |",
                -120000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-02",
                R.drawable.icon_food_category,
                "희진이 간식비 (예정)",
                "희진이 복지 |",
                -7700.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-03",
                R.drawable.icon_food_category,
                "지환이 지각비 (예정)",
                "지환이 복지 |",
                10000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-03",
                R.drawable.icon_food_category,
                "그 외 Title (예정)",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-03",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-05",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-10",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-10",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-10",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-11",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-12",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-13",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-14",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-15",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-20",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            ),
            TransactionRecyclerDataClass(
                "2024-11-20",
                R.drawable.icon_food_category,
                "그 외 Title",
                "그 외 category |",
                -1000.0
            )
        )


// 클릭 리스너를 람다로 전달
        recyclerViewAdapter = TransactionRecyclerAdapter(budgetData, isForBudget = true) {
            // CameraActiDLvity로 이동
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }

        binding.recyclerViewBudget.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BudgetFragment.
         */
        // TODO: Rename and change types and number of parameters
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