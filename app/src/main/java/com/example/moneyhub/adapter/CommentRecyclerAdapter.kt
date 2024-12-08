package com.example.moneyhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.moneyhub.databinding.ItemLayoutCommentBinding
import com.example.moneyhub.model.Comment

class CommentRecyclerAdapter(
    private var comments: List<Comment>,
    private val currentUserId: String,
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {

    fun updateComments(newComments: List<Comment>) {
        this.comments = newComments
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLayoutCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.tvCommentAuthor.text = comment.authorName
            binding.tvCommentContent.text = comment.content

            // 작성자인 경우만 수정/삭제 버튼 보이게
            if (comment.authorId == currentUserId) {
                binding.btnEditComment.visibility = View.VISIBLE
                binding.btnDeleteComment.visibility = View.VISIBLE
            } else {
                binding.btnEditComment.visibility = View.GONE
                binding.btnDeleteComment.visibility = View.GONE
            }

            binding.btnEditComment.setOnClickListener {
                onEditClick(comment)
            }

            binding.btnDeleteComment.setOnClickListener {
                onDeleteClick(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount() = comments.size
}
