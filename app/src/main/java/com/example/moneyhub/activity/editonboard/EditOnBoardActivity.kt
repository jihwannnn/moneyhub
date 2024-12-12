package com.example.moneyhub.activity.editonboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityEditOnBoardBinding
import com.example.moneyhub.fragments.board.BoardFragment
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.PostSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditOnBoardBinding
    private val viewModel: EditOnBoardViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
            if (uri != null) {
                binding.ivEditPreview.visibility = View.VISIBLE // 이미지 미리보기 보이기
                Glide.with(this)
                    .load(uri) // Uri로 이미지 로드
                    .into(binding.ivEditPreview) // ivEditPreview에 바인딩
            } else {
                binding.ivEditPreview.visibility = View.GONE // Uri가 없으면 숨기기
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // 현재 게시글 가져오기
        val post = PostSession.getCurrentPost()
        viewModel.fetchPost(post)

        // 백 버튼 클릭 시
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 이미지 변경 버튼 클릭 시
        binding.btnEditAddImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // 저장 버튼 클릭 시
        binding.btnSave.setOnClickListener {
            val newTitle = binding.etEditTitle.text.toString().trim()
            val newContent = binding.etEditContent.text.toString().trim()

            viewModel.updatePost(
                newTitle = newTitle,
                newContent = newContent,
                newImageUri = selectedImageUri
            )
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
                binding.btnSave.isEnabled = !state.isLoading
            }
        }

        // 게시글 정보 관찰
        lifecycleScope.launch {
            viewModel.currentPost.collect { post ->
                if (post != null) {
                    binding.apply {
                        etEditTitle.setText(post.title)
                        etEditContent.setText(post.content)
                        if (post.imageUrl.isNotEmpty()) {
                            ivEditPreview.visibility = View.VISIBLE
                            Glide.with(this@EditOnBoardActivity)
                                .load(post.imageUrl)
                                .into(ivEditPreview)
                        } else {
                            ivEditPreview.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        Toast.makeText(this, "처리 중...", Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccess() {
        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun handleError(error: String?) {
        Toast.makeText(this, error ?: "처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
    }
}
