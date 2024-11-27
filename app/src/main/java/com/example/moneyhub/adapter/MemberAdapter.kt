package com.example.moneyhub.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.databinding.CustomMemberItemBinding
import com.example.moneyhub.model.Member

// RecyclerView에 데이터를 바인딩하기 위한 어댑터 클래스
class MemberAdapter(
    private val currentUserId: String,  // 현재 로그인한 사용자 ID
    private val isLeader: Boolean,      // 현재 사용자가 대표인지
    private val onLeaveGroup: () -> Unit,  // 그룹 나가기 콜백
    private val onPromoteMember: (String, String) -> Unit,  // 멤버 승급 콜백 (userId, newRole)
    private val onDemoteMember: (String) -> Unit,  // 멤버 강등 콜백
    private val onKickMember: (String) -> Unit     // 멤버 추방 콜백
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    private val members = mutableListOf<Member>()

    fun updateMembers(newMembers: List<Member>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomMemberItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member)

        holder.itemView.setOnClickListener {
            if (member.id == currentUserId) {
                if (member.role == "대표") {
                    showLeaderLeaveDialog(member, holder.itemView.context)
                } else {
                    showLeaveDialog(holder.itemView.context)
                }
            } else if (isLeader) {
                when (member.role) {
                    "멤버" -> showMemberOptionsDialog(member, holder.itemView.context)
                    "매니저" -> showManagerOptionsDialog(member, holder.itemView.context)
                }
            }
        }
    }

    override fun getItemCount() = members.size

    private fun showLeaderLeaveDialog(leader: Member, context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("대표 위임")
            val eligibleMembers = members.filter { it.id != leader.id }
            val memberNames = eligibleMembers.map { it.name }.toTypedArray()

            setSingleChoiceItems(memberNames, -1) { dialog, which ->
                val newLeader = eligibleMembers[which]
                onPromoteMember(newLeader.id, "대표")
                onLeaveGroup()
                dialog.dismiss()
            }
            setNegativeButton("취소", null)
        }.show()
    }

    private fun showLeaveDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("모임 나가기")
            setMessage("정말 모임을 나가시겠습니까?")
            setPositiveButton("나가기") { _, _ -> onLeaveGroup() }
            setNegativeButton("취소", null)
        }.show()
    }

    private fun showMemberOptionsDialog(member: Member, context: Context) {
        val options = arrayOf("매니저로 승급", "모임에서 내보내기")
        AlertDialog.Builder(context).apply {
            setTitle("멤버 관리")
            setItems(options) { _, which ->
                when (which) {
                    0 -> onPromoteMember(member.id, "매니저")
                    1 -> onKickMember(member.id)
                }
            }
        }.show()
    }

    private fun showManagerOptionsDialog(member: Member, context: Context) {
        val options = arrayOf("대표로 승급", "멤버로 강등", "모임에서 내보내기")
        AlertDialog.Builder(context).apply {
            setTitle("매니저 관리")
            setItems(options) { _, which ->
                when (which) {
                    0 -> onPromoteMember(member.id, "대표")
                    1 -> onDemoteMember(member.id)
                    2 -> onKickMember(member.id)
                }
            }
        }.show()
    }

    class ViewHolder(
        private val binding: CustomMemberItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            with(binding) {
                tvMemberName.text = member.name

                btnStatus.apply {
                    text = member.role
                    backgroundTintList = ColorStateList.valueOf(
                        when (member.role) {
                            "대표" -> ContextCompat.getColor(context, R.color.moneyBlue)
                            "매니저" -> ContextCompat.getColor(context, R.color.moneyCyanThick)
                            else -> ContextCompat.getColor(context, R.color.moneyGrey)
                        }
                    )
                }
            }
        }
    }
}