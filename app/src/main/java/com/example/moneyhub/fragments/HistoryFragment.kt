package com.example.moneyhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.activity.RegisterDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentHistoryBinding
import com.example.moneyhub.model.Transaction

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHistoryBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter

    private val historyData = mutableListOf(
        Transaction(
            tid = "11",
            title = "간식 사업 지출",
            category = "학생 복지 |",
            type = false, // 지출
            amount = -120000,
            content = "",
            payDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse("2024-11-10").time,
            verified = true,
            createdAt = System.currentTimeMillis()
        ),
        Transaction(
            tid = "12",
            title = "그 외 Title",
            category = "그 외 category |",
            type = false, // 지출
            amount = -1000,
            content = "",
            payDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse("2024-11-10").time,
            verified = true,
            createdAt = System.currentTimeMillis()
        ),
        Transaction(
            tid = "13",
            title = "그 외 Title",
            category = "그 외 category |",
            type = false, // 지출
            amount = -1000,
            content = "",
            payDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse("2024-11-10").time,
            verified = true,
            createdAt = System.currentTimeMillis()
        )
    )

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
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Initializing RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // adapter setting
        adapter = TransactionAdapter(historyData, false)
        recyclerView.adapter = adapter

        // 버튼 클릭 리스너 추가
        binding.btnAddHistory.setOnClickListener {
            val intent = Intent(requireActivity(), RegisterDetailsActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
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