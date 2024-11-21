package com.example.moneyhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.adapter.TransactionRecyclerAdapter
import com.example.moneyhub.data.model.TransactionRecyclerDataClass
import com.example.moneyhub.databinding.FragmentHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    private lateinit var adapter: TransactionRecyclerAdapter

    private val historyData = listOf(
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "간식 사업 지출", "학생 복지 |", -120000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "희진이 간식비", "희진이 복지 |", -7700.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "지환이 노래방", "지환이 복지 |", -10000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "정기 회비", "그 외 category |", 100000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0),
        TransactionRecyclerDataClass("2024-11-10",R.drawable.icon_food_category, "그 외 Title", "그 외 category |", -1000.0)
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
        adapter = TransactionRecyclerAdapter(historyData, false)
        recyclerView.adapter = adapter

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