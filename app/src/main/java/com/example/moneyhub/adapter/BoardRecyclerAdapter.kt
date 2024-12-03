package com.example.moneyhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneyhub.R
import com.example.moneyhub.activity.ViewOnBoardActivity
import com.example.moneyhub.databinding.ItemLayoutBoardBinding
import com.example.moneyhub.model.Post
import com.example.moneyhub.utils.DateUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

class BoardRecyclerAdapter (
    private val posts: List<Post>,
    private val context: Context,
    ) :
    RecyclerView.Adapter<BoardRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRecyclerAdapter.ViewHolder {
        val binding = ItemLayoutBoardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardRecyclerAdapter.ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)

        // 이미지 처리
        with(holder.binding) {
            if (post.imageUrl != "") {
                postImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(post.imageUrl)
                    .into(postImage)
            } else {
                postImage.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            // 새 액티비티로 전환
            val intent = Intent(holder.itemView.context, ViewOnBoardActivity::class.java)
            intent.putExtra("post_id", post.pid)   // group id 전달
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(
        val binding: ItemLayoutBoardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                val localDateTime = DateUtils.millisToLocalDateTime(post.createdAt)
                postTitle.text = post.title
                postContent.text = post.content
                postTimeAgo.text = getTimeAgo(localDateTime, itemView.context)
                postCommentCount.text = "${post.commentCount}"
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    companion object {
        fun getTimeAgo(postTime: LocalDateTime, context: android.content.Context): String {
            val now = LocalDateTime.now()
            val duration = Duration.between(postTime, now)

            return when {
                duration.toMinutes() < 60 -> {
                    val minutes = duration.toMinutes().toInt()
                    context.resources.getQuantityString(R.plurals.time_minutes_ago, minutes, minutes)
                }
                duration.toHours() < 24 -> {
                    val hours = duration.toHours().toInt()
                    context.resources.getQuantityString(R.plurals.time_hours_ago, hours, hours)
                }
                duration.toDays() < 7 -> {
                    val days = duration.toDays().toInt()
                    context.resources.getQuantityString(R.plurals.time_days_ago, days, days)
                }
                else -> postTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            }
        }
    }
}