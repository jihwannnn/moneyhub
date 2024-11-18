package com.example.moneyhub.adapter

import android.content.res.ColorStateList // 버튼 배경색 지정에 필요
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat // 색상 리소스 접근에 필요
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

        // 멤버 데이터를 뷰에 바인딩
        fun bind(member: Member) {
            with(binding) {
                // 이름 설정
                tvMemberName.text = member.name

                // 상태 버튼 설정
                btnStatus.apply {
                    text = member.status
                    // 상태에 따라 버튼 배경색 동적 변경
                    backgroundTintList = ColorStateList.valueOf(
                        when (member.status) {
                            "매니저" -> ContextCompat.getColor(context, R.color.moneyCyanRegular)
                            else -> ContextCompat.getColor(context, R.color.moneyGrey)
                        }
                    )
                }
            }
        }
    }
}