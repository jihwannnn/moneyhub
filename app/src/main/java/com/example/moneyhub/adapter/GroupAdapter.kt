package com.example.moneyhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.databinding.CustomGroupItemBinding
import com.example.moneyhub.model.UserGroup

class GroupAdapter(
    private val onGroupClick: (String, String) -> Unit
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    private var userGroup: UserGroup = UserGroup()

    fun updateData(newUserGroup: UserGroup) {
        userGroup = newUserGroup
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomGroupItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupEntry = userGroup.groups.entries.elementAt(position)
        holder.bind(groupEntry.value)
        holder.itemView.setOnClickListener {
            onGroupClick(groupEntry.key, groupEntry.value)
        }
    }

    override fun getItemCount() = userGroup.groups.size

    class ViewHolder(
        private val binding: CustomGroupItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupName: String) {
            binding.tvGroupName.text = groupName
        }
    }
}