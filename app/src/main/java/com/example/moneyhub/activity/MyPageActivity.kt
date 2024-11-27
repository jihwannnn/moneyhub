package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.adapter.GroupAdapter
import com.example.moneyhub.data.model.GroupItem
import com.example.moneyhub.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupAdapter

    // 샘플 모임 데이터
    private val groupsData = listOf(
        GroupItem(1, "가족 모임"),
        GroupItem(2, "회사 동호회"),
        GroupItem(3, "친구 여행")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupClickListeners()

        // RecyclerView 초기화
        recyclerView = binding.groupList
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter 설정
        adapter = GroupAdapter(groupsData, this)
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        // 정보수정 버튼 설정
        with(binding.btnEditInfo) {
            root.setBackgroundResource(R.drawable.yellow)
            btnText.apply {
                text = "정보수정"
//                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        // 로그아웃 버튼
        with(binding.btnLogOut) {
            root.setBackgroundResource(R.drawable.gray)
            btnText.apply {
                text = "로그아웃"
//                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        // 방만들기 버튼 설정
        with(binding.btnCreateRoom) {
            root.setBackgroundResource(R.drawable.emerald)
            btnText.apply {
                text = "방만들기"
//                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    private fun setupClickListeners() {
        // 정보수정 버튼 클릭 리스너
        binding.btnEditInfo.root.setOnClickListener {
            // 정보수정 처리
        }

        // 방만들기 버튼 클릭 리스너
        binding.btnCreateRoom.root.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }
}
