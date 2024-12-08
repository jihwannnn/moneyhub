package com.example.moneyhub.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.activity.creategroup.CreateActivity
import com.example.moneyhub.activity.main.MainActivity
import com.example.moneyhub.adapter.GroupAdapter
import com.example.moneyhub.databinding.ActivityMyPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = GroupAdapter(
            onGroupClick = { groupId, groupName ->
                viewModel.selectGroup(groupId, groupName)
            }
        )

        binding.groupList.apply {
            layoutManager = LinearLayoutManager(this@MyPageActivity)
            adapter = this@MyPageActivity.adapter
        }
    }

    private fun setupButtons() {
        binding.btnEditInfo.apply {
            root.setBackgroundResource(R.drawable.yellow)
            btnText.text = "정보수정"
        }

        binding.btnLogOut.apply {
            root.setBackgroundResource(R.drawable.gray)
            btnText.text = "로그아웃"
        }

        binding.btnCreateRoom.apply {
            root.setBackgroundResource(R.drawable.emerald)
            btnText.text = "방만들기"
            root.setOnClickListener {
                startActivity(Intent(this@MyPageActivity, CreateActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                binding.tvName.text = "${user?.name ?: "Unknown"} 님"
            }
        }

        lifecycleScope.launch {
            viewModel.userGroups.collect { userGroup ->
                userGroup?.let {
                    adapter.updateData(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        // Show loading if needed
                    }
                    state.isSuccess -> {
                        // 그룹 선택 성공 시 MainActivity로 이동
                        startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
                        finish()
                    }

                    state.error != null -> {
                        Toast.makeText(this@MyPageActivity, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
