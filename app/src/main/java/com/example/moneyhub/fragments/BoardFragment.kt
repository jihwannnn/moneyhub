package com.example.moneyhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneyhub.activity.postonboard.PostOnBoardActivity
import com.example.moneyhub.adapter.BoardRecyclerAdapter
import com.example.moneyhub.databinding.FragmentBoardBinding
import com.example.moneyhub.model.Post

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
    lateinit var binding: FragmentBoardBinding

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

        binding.fabPost.setOnClickListener{
            val intent = Intent(context, PostOnBoardActivity::class.java)
            startActivity(intent)
        }

        val postList = listOf(
            Post(
                "1",
                "1",
                "Post Title 1",
                "This is the first post.",
                "1",
                "Seongmin",
                "https://example.com/image1.jpg",
                5,
                System.currentTimeMillis()-60*1000
            ),
            Post(
                "2",
                "1",
                "Post Title 2",
                "This is the second post.",
                authorId = "1",
                authorName = "Jihwan",
                "",
                3,
                System.currentTimeMillis()-60*1000*30
            ), // 이미지 없음
            Post(
                "3",
                "1",
                "Post Title 3",
                "This is the third post.",
                authorId = "1",
                authorName = "Jihwan",
                "https://example.com/image2.jpg",
                10,
                System.currentTimeMillis()-60*1000*60*24*5
            ),
            Post(
                "4",
                "1",
                "Post Title 4",
                "This is the fourth post.",
                authorId = "1",
                authorName = "Huijin",
                "",
                0,
                System.currentTimeMillis()-60*1000*60*24*10
            ),
            Post(
                "5",
                "1",
                "교양 취업계",
                "막학기 14학점 남았는데 전공학점은 다 채웠고 교양만 들으면 되는데 취업계가 가능한가요? 교양 교수님들도 취업계 인정 해주시나요",
                authorId = "1",
                authorName = "wow",
                "https://example.com/image1.jpg",
                5,
                System.currentTimeMillis()-60*1000*60*24*11
            ),
            Post(
                "6",
                "1",
                "선배님들 차 사는거 어케 생각하심??",
                "아부지 차로 연습을 하는중인데 차 한데 사려고 함\n" +
                        "업무 특성상 여기저기 돌아다녀야 해서 하나 사려는데\n" +
                        "첫차로 새차를 살까요 아니면 굴러가는 중고 사서 연습을 할까요?",
                authorId = "1",
                authorName = "Jihwan",
                "",
                3,
                System.currentTimeMillis()-60*1000*60*24*23
            ),
            Post(
                "7",
                "1",
                "멘토링",
                "저희 학교 현장실습지원센터에서 지원해주는 멘토링 활동 있나요?",
                authorId = "1",
                authorName = "Jihwan",
                "https://example.com/image2.jpg",
                10,
                System.currentTimeMillis()-60L*1000L*60L*24L*25L
            ),
            Post(
                "8",
                "1",
                "동기들은 다 잘나가는데",
                "왜 나만...?",
                authorId = "1",
                authorName = "Jihwan",
                "",
                0,
                System.currentTimeMillis()-60L*1000L*60L*24L*80L
            )
        )

        val adapter = BoardRecyclerAdapter(postList, requireContext()) // Adapter Creation
        binding.recyclerViewBoard.adapter = adapter
        binding.recyclerViewBoard.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
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