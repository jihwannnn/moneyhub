package com.example.moneyhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.databinding.CustomMemberItemBinding
import com.example.moneyhub.data.model.Member

// RecyclerView에 데이터를 바인딩하기 위한 어댑터 클래스
class MemberAdapter : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    // 표시할 멤버 데이터 리스트
    private val members = listOf(
        Member("김회장", "매니저"),
        Member("김부회장", "매니저"),
        Member("김평범", "멤버"),
        Member("나", "멤버")
    )

    // 새로운 ViewHolder 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomMemberItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member)
    }

    // 전체 아이템 개수 반환
    override fun getItemCount() = members.size

    // 각 멤버 아이템의 뷰를 보관하는 ViewHolder 클래스
    class ViewHolder(
        private val binding: CustomMemberItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            with(binding) {
                tvMemberName.text = member.name
                btnStatus.apply {
                    text = member.status
                    // 로그 추가
                    println("멤버 상태: ${member.status}")
                    setBackgroundResource(
                        when (member.status) {
                            "매니저" -> {
                                println("매니저 배경 적용")
                                R.drawable.emerald
                            }

                            else -> {
                                println("회색 배경 적용")
                                R.drawable.gray
                            }
                        }
                    )
                }
            }
        }
    }
}