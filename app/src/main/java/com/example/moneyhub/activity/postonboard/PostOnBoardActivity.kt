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
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
            if (uri != null) {
                binding.ivPreview.visibility = View.VISIBLE // 이미지 미리보기 보이기
                Glide.with(this)
                    .load(uri) // Uri로 이미지 로드
                    .into(binding.ivPreview) // ivPreview에 바인딩
            } else {
                binding.ivPreview.visibility = View.GONE // Uri가 없으면 숨기기
            }
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
                val title = etTitle.text.toString()
                val content = etContent.text.toString()

                // Example authorId and authorName. Replace with actual user data.
                viewModel.post(
                    title = etTitle.text.toString(),
                    content = etContent.text.toString(),
                    imageUri = imageUri
                )

                finish()
            }

            // ViewModel 상태에 따라 버튼 활성화/비활성화
            lifecycleScope.launch {
                viewModel.uiState.collect { state ->
                    // 버튼이 비활성화일 경우 클릭 불가 및 투명도 조정
                    customButtonIncludePost.customButton.apply {
                        isClickable = !state.isLoading
                        alpha = if (state.isLoading) 0.5f else 1.0f
                    }
                }
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
                binding.customButtonIncludePost.customButton.isEnabled = !state.isLoading
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