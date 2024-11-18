package com.example.moneyhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneyhub.adapter.BoardRecyclerAdapter
import com.example.moneyhub.data.model.BoardItem
import com.example.moneyhub.databinding.FragmentBoardBinding
import java.time.LocalDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding:FragmentBoardBinding

    override fun onCreate(savedInstnaceState: Bundle?) {
        super.onCreate(savedInstnaceState)
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
        binding = FragmentBoardBinding.inflate(inflater, container, false)

        val postList = listOf(
            BoardItem(1, "Post Title 1", "This is the first post.", LocalDateTime.now().minusMinutes(30), 5, "https://example.com/image1.jpg"),
            BoardItem(2, "Post Title 2", "This is the second post.", LocalDateTime.now().minusHours(2), 3, null), // 이미지 없음
            BoardItem(3, "Post Title 3", "This is the third post.", LocalDateTime.now().minusDays(5), 10, "https://example.com/image2.jpg"),
            BoardItem(4, "Post Title 4", "This is the fourth post.", LocalDateTime.now().minusDays(10), 0, null) // 이미지 없음
        )

        val adapter = BoardRecyclerAdapter(postList) // Adapter Creation
        binding.recyclerViewBoard.adapter = adapter
        binding.recyclerViewBoard.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}