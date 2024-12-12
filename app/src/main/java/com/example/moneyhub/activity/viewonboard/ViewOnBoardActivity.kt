package com.example.moneyhub.activity.viewonboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
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
import com.example.moneyhub.activity.editonboard.EditOnBoardActivity
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.activity.postonboard.PostOnBoardActivity
import com.example.moneyhub.adapter.CommentRecyclerAdapter
import com.example.moneyhub.databinding.ActivityViewOnBoardBinding
import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.model.sessions.PostSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewOnBoardBinding
    private val viewModel: ViewOnBoardViewModel by viewModels()
    private lateinit var commentAdapter: CommentRecyclerAdapter
    private var isDeleting: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
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

        binding.btnContainer.apply {
            binding.btnEdit.setOnClickListener {
                // EditOnBoardActivity로 이동
                PostSession.setPost(post)
                val intent = Intent(this@ViewOnBoardActivity, EditOnBoardActivity::class.java)
                startActivity(intent)
            }

            binding.btnDelete.setOnClickListener {
                showDeleteConfirmationDialog(post)
            }
        }

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
            val content = binding.etComment.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.addComment(content)
                binding.etComment.setText("")

                // 부모 레이아웃에 포커스 요청
                binding.root.requestFocus()

                // 키보드 내리기
                hideKeyboard()
            } else {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(this)
            .setTitle("게시글 삭제")
            .setMessage("정말로 게시글을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, which ->
                // 삭제 작업 시작을 표시
                isDeleting = true
                // ViewModel을 통해 게시글 삭제
                viewModel.deletePost(post)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0) // EditText의 windowToken 직접 사용
        // EditText의 포커스 해제
        binding.etComment.clearFocus()
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

                    // 게시글 작성자인지 확인하고 버튼 가시성 설정
                    val currentUser = viewModel.currentUser.value
                    val isAuthor = currentUser?.id == post.authorId

                    binding.btnContainer.visibility =
                        if (isAuthor) View.VISIBLE else View.GONE
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
                when {
                    state.isSuccess -> handleDeleteSuccess()
                    state.error != null -> handleError(state.error)
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
                // 게시글 작성자인지 확인
                val post = viewModel.currentPost.value
                val isAuthor = post?.authorId == user?.id

                binding.btnContainer.visibility =
                    if (isAuthor) View.VISIBLE else View.GONE

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

    private fun handleDeleteSuccess() {
        Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        // BoardFragment로 돌아가기
        finish() // 현재 액티비티 종료
    }

    private fun handleError(error: String?) {
        Toast.makeText(this, error ?: "처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
    }
}