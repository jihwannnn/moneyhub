package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneyhub.R
import com.example.moneyhub.activity.postonboard.PostOnBoardViewModel
import com.example.moneyhub.databinding.ActivityViewOnBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewOnBoardBinding
    private val viewModel: PostOnBoardViewModel by viewModels()

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
    }

    private fun setupUI() {

        val postId = intent.getStringExtra("post_id") ?: return
        viewModel.fetchPost(postId)

        binding.apply {
            customHeaderInclude.imageViewMyPage.setOnClickListener{
                val intent = Intent(this@ViewOnBoardActivity, MyPageActivity::class.java)
                startActivity(intent)
            }
        }
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
}