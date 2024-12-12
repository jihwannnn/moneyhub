package com.example.moneyhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneyhub.activity.viewonboard.ViewOnBoardActivity
import com.example.moneyhub.databinding.ItemLayoutBoardBinding
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.PostSession
import com.example.moneyhub.utils.DateUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

class BoardRecyclerAdapter(
    private var posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<BoardRecyclerAdapter.ViewHolder>() {

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutBoardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)

        handlePostImage(holder, post)

        setupClickListener(holder, post)
    }

    private fun handlePostImage(holder: ViewHolder, post: Post) {
        with(holder.binding) {
            if (post.imageUrl.isNotEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(post.imageUrl)
                    .into(postImage)
            } else {
                postImage.visibility = View.GONE
            }
        }
    }

    private fun setupClickListener(holder: ViewHolder, post: Post) {
        holder.itemView.setOnClickListener {
            navigateToPostDetail(holder.itemView.context, post)
        }
    }

    private fun navigateToPostDetail(context: Context, post: Post) {
        val intent = Intent(context, ViewOnBoardActivity::class.java)
        PostSession.setPost(post)
        context.startActivity(intent)
    }

    class ViewHolder(
        val binding: ItemLayoutBoardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                val localDateTime = DateUtils.millisToLocalDateTime(post.createdAt)
                postTitle.text = post.title
                postContent.text = post.content
                postTimeAgo.text = getTimeAgo(localDateTime)
                postCommentCount.text = post.commentCount.toString()
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    companion object {
        fun getTimeAgo(postTime: LocalDateTime): String {
            val now = LocalDateTime.now()
            val duration = Duration.between(postTime, now)

            return when {
                duration.toMinutes() < 60 -> {
                    val minutes = duration.toMinutes().toInt()
                    if (minutes == 1) "1 minute ago" else "$minutes minutes ago"
                }
                duration.toHours() < 24 -> {
                    val hours = duration.toHours().toInt()
                    if (hours == 1) "1 hour ago" else "$hours hours ago"
                }
                duration.toDays() < 7 -> {
                    val days = duration.toDays().toInt()
                    if (days == 1) "1 day ago" else "$days days ago"
                }
                else -> postTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            }
        }
    }
}