package com.example.moneyhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.activity.MainActivity
import com.example.moneyhub.data.model.GroupItem
import com.example.moneyhub.databinding.CustomGroupItemBinding

// Mypage의 RecyclerView에 데이터를 바인딩하기 위한 어댑터 클래스
class GroupAdapter (
    private val groups: List<GroupItem>,
    private val context: Context,
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.ViewHolder {
        val binding = CustomGroupItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupAdapter.ViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)

        holder.itemView.setOnClickListener {
            // 새 액티비티로 전환
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("group_id", group.id)   // group id 전달
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = groups.size

    class ViewHolder(
        private val binding: CustomGroupItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: GroupItem) {
            with(binding) {
                tvGroupName.text = group.groupName
            }
        }
    }
}