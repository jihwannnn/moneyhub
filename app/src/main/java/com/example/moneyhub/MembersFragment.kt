package com.example.moneyhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.adapter.MemberAdapter
import com.example.moneyhub.data.model.Member
import com.example.moneyhub.databinding.FragmentMembersBinding
import com.google.firebase.auth.FirebaseAuth // Firebase 인증 추가
import com.google.firebase.firestore.FirebaseFirestore // Firestore 추가

class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    private lateinit var memberAdapter: MemberAdapter
    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
//    private val currentUser = FirebaseAuth.getInstance().currentUser // 현재 로그인한 사용자
    private val currentUser = FirebaseAuth.getInstance().currentUser // FirebaseUser?


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadMembers() // 멤버 데이터 로드
    }

    // RecyclerView 초기화 및 설정
    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(
            currentUserId = currentUser?.uid ?: "",
            isLeader = true, // TODO: Firestore에서 현재 사용자의 역할 확인
            onLeaveGroup = { leaveGroup() },
            onPromoteMember = { userId, newRole -> promoteMember(userId, newRole) },
            onDemoteMember = { userId -> demoteMember(userId) },
            onKickMember = { userId -> kickMember(userId) }
        )

        val sampleMembers = listOf(
            Member("user1", "김회장", "대표"),
            Member("user2", "박매니저", "매니저"),
            Member("user3", "이매니저", "매니저"),
            Member("user4", "김멤버", "멤버"),
            Member("user5", "나", "멤버")
        )
        memberAdapter.updateMembers(sampleMembers)

        binding.memberList.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    // Firestore에서 멤버 데이터 로드
    private fun loadMembers() {
        // TODO: Firestore에서 멤버 데이터 가져오기
        // db.collection("groups").document(groupId).collection("members")...
    }

    // 그룹 나가기 처리
    private fun leaveGroup() {
        // TODO: Firestore에서 멤버 제거
    }

    // 멤버 승급 처리
    private fun promoteMember(userId: String, newRole: String) {
        // TODO: Firestore에서 멤버 역할 업데이트
    }

    // 멤버 강등 처리
    private fun demoteMember(userId: String) {
        // TODO: Firestore에서 멤버 역할 업데이트
    }

    // 멤버 추방 처리
    private fun kickMember(userId: String) {
        // TODO: Firestore에서 멤버 제거
    }

    companion object {
        @JvmStatic
        fun newInstance() = MembersFragment()
    }
}