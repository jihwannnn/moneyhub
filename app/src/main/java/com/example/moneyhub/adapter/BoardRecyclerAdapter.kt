package com.example.moneyhub.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneyhub.R
import com.example.moneyhub.data.model.BoardItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

class BoardRecyclerAdapter (private val items: List<BoardItem>) :
    RecyclerView.Adapter<BoardRecyclerAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.postTitle)
        val content: TextView = itemView.findViewById(R.id.postContent)
        val timeAgo: TextView = itemView.findViewById(R.id.postTimeAgo)
        val commentCount: TextView = itemView.findViewById(R.id.postCommentCount)
        val imageView: ImageView = itemView.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_board, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.content.text = item.content
        holder.timeAgo.text = getTimeAgo(item.timestamp)
        holder.commentCount.text = "${item.commentCount}"

        // 이미지 처리
        if (item.imageUrl != null) {
            holder.imageView.visibility = View.VISIBLE
            // 이미지 로드 (Glide 또는 Picasso 사용)
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.icon_food_category) // 로딩 중 표시할 이미지
                .into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }



    }

    override fun getItemCount(): Int = items.size

    fun getTimeAgo(postTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(postTime, now)

        return when {
            duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
            duration.toHours() < 24 -> "${duration.toHours()}시간 전"
            duration.toDays() < 7 -> "${duration.toDays()}일 전"
            else -> postTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        }
    }
}