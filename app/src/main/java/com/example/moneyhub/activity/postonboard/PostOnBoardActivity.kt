package com.example.moneyhub.activity.postonboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityPostOnBoardBinding
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostOnBoardBinding
    private val viewModel: PostOnBoardViewModel by viewModels()
    private var imageUri = ""

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri?.toString() ?: ""
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initializing view binding
        binding = ActivityPostOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // 글쓰기 종료 버튼
            btnClose.setOnClickListener {
                finish()
            }

            // 이미지 선택 버튼
            btnAddImage.setOnClickListener {
                pickImage.launch("image/*")
            }


            // 게시 버튼
            customButtonIncludePost.customButton.setOnClickListener {
                // Example authorId and authorName. Replace with actual user data.
                viewModel?.post(
                    title = etTitle.text.toString(),
                    content = etContent.text.toString(),
                    authorId = "user123",
                    authorName = "John Doe",
                    groupId = "group456",
                    imageUri = imageUri
                )
            }
        }
    }

    private fun observeViewModel() {
        // UI 상태 관찰
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> showLoading()
                    state.isSuccess -> handleSuccess()
                    state.error != null -> handleError(state.error)
                }
            }
        }
    }

    private fun showLoading() {
        Toast.makeText(this, "업로드 중...", Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccess() {
        Toast.makeText(this, "게시물이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleError(error: String?) {
        Toast.makeText(this, error ?: "게시물 업로드 실패.", Toast.LENGTH_SHORT).show()
    }
}