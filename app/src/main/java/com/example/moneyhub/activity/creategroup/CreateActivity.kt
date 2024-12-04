package com.example.moneyhub.activity.creategroup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.databinding.ActivityCreateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private val viewModel: CreateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()
        setupViews()
        observeViewModel()
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        binding.buttonCreateGroup.setOnClickListener {
            val groupName = binding.groupNameInput.text.toString()
            viewModel.createGroup(groupName)
        }

        binding.customHeaderInclude.imageViewMyPage.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        // Show loading indicator if needed
                    }
                    state.isSuccess -> {
                        Toast.makeText(this@CreateActivity, "그룹이 생성되었습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CreateActivity, MyPageActivity::class.java))
                        finish()
                    }
                    state.error != null -> {
                        Toast.makeText(this@CreateActivity, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}