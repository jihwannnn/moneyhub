package com.example.moneyhub.activity.viewonboard

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.moneyhub.R
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.activity.postonboard.PostOnBoardViewModel
import com.example.moneyhub.adapter.CommentRecyclerAdapter
import com.example.moneyhub.databinding.ActivityViewOnBoardBinding
import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.sessions.PostSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewOnBoardBinding
    private val viewModel: ViewOnBoardViewModel by viewModels()
    private lateinit var commentAdapter: CommentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initializing view binding
        binding = ActivityViewOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observePost()
        observeViewModel()
    }

    private fun setupUI() {

        val post = PostSession.getCurrentPost()
        viewModel.fetchPost(post)

        binding.customHeaderInclude.imageViewMyPage.setOnClickListener{
                val intent = Intent(this@ViewOnBoardActivity, MyPageActivity::class.java)
                startActivity(intent)
        }

        // 댓글 RecyclerView 초기화
        commentAdapter = CommentRecyclerAdapter(
            comments = emptyList(),
            currentUserId = viewModel.currentUser.value?.id ?: "",
            onEditClick = { comment -> showEditCommentDialog(comment) },
            onDeleteClick = { comment -> viewModel.deleteComment(comment) }
        )

        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(this@ViewOnBoardActivity)
            adapter = commentAdapter
        }

        binding.btnAddComment.setOnClickListener {
            val content = binding.etComment.text.toString()
            viewModel.addComment(content)
            binding.etComment.setText("")
        }
    }

    private fun showEditCommentDialog(comment: Comment) {
        val editText = EditText(this)
        editText.setText(comment.content)

        AlertDialog.Builder(this)
            .setTitle("Edit Comment")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newContent = editText.text.toString().trim()
                viewModel.editComment(comment, newContent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observePost() {
        lifecycleScope.launch {
            viewModel.currentPost.collect { post ->
                if (post != null) {
                    binding.apply {
                        tvTitle.text = post.title
                        tvContent.text = post.content
                        Glide.with(this@ViewOnBoardActivity)
                            .load(post.imageUrl)
                            .into(ivPhoto)
                    }
                } else {
                    // 게시글을 찾을 수 없을 경우 처리
                    binding.tvTitle.text = "게시글을 찾을 수 없습니다."
                    binding.tvContent.text = ""
                }
            }
        }
    }

    private fun observeViewModel() {
        // UI 상태 관찰
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    // 로딩 표시 가능
                }
                state.error?.let {
                    Toast.makeText(this@ViewOnBoardActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 댓글 리스트 관찰
        lifecycleScope.launch {
            viewModel.commentList.collect { comments ->
                commentAdapter.updateComments(comments)
            }
        }

        // currentUser 변화를 관찰하여 Edit/Delete 버튼 표시 여부 갱신
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                // user 정보가 바뀌면 adapter도 업데이트 필요
                commentAdapter = CommentRecyclerAdapter(
                    comments = viewModel.commentList.value,
                    currentUserId = user?.id ?: "",
                    onEditClick = { comment -> showEditCommentDialog(comment) },
                    onDeleteClick = { comment -> viewModel.deleteComment(comment) }
                )
                binding.recyclerViewComments.adapter = commentAdapter
            }
        }
    }
}