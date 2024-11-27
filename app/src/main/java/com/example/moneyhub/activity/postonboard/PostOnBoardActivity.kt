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
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostOnBoardBinding
    private val viewModel: PostOnBoardViewModel by viewModels()

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.updateImageUri(it.toString()) }
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

            // 제목 및 내용 업데이트
            etTitle.doOnTextChanged { text, _, _, _ ->
                viewModel?.updateTitle(text?.toString() ?: "")
            }

            // 제목 및 내용 업데이트
            etContent.doOnTextChanged { text, _, _, _ ->
                viewModel?.updateContent(text?.toString() ?: "")
            }

            // 게시 버튼
            customButtonIncludePost.customButton.setOnClickListener {
                // Example authorId and authorName. Replace with actual user data.
                viewModel?.post(
                    authorId = "user123",
                    authorName = "John Doe",
                    groupId = "group456"
                )
            }
        }
    }

    private fun observeViewModel() {
        // UI 상태 관찰
        viewModel.uiState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    UiState.LOADING -> showLoading()
                    UiState.SUCCESS -> handleSuccess()
                    UiState.ERROR -> handleError()
                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)

        // 에러 메시지 관찰
        viewModel.errorMessage
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { message ->
                message?.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
            .launchIn(lifecycleScope)

        // 이미지 미리보기 업데이트
        viewModel.imageUri
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { uri ->
                binding.ivPreview.setImageURI(uri?.let { Uri.parse(it) })
            }
            .launchIn(lifecycleScope)
    }

    private fun showLoading() {
        Toast.makeText(this, "업로드 중...", Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccess() {
        Toast.makeText(this, "게시물이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleError() {
        Toast.makeText(this, "게시물 업로드 실패.", Toast.LENGTH_SHORT).show()
    }
}