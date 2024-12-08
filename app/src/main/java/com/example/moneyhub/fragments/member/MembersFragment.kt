package com.example.moneyhub.fragments.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.adapter.MemberAdapter
import com.example.moneyhub.databinding.FragmentMembersBinding
import com.example.moneyhub.model.Role
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    private lateinit var memberAdapter: MemberAdapter
    private val viewModel: MembersViewModel by viewModels()

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
        setupViews()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViews() {
        // 그룹 이름 설정
        binding.customGroupbarYellowInclude.textViewGroupName.text = viewModel.currentGroupName.value

        // 링크 버튼 설정
        binding.linkButtonInclude.apply {
            root.setOnClickListener {
                context?.let { ctx ->
                    viewModel.copyGroupId(ctx)
                    Toast.makeText(ctx, "초대 코드가 복사되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
            linkBtnText.text = "Copy the Link"
        }
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(
            currentUserId = viewModel.currentUser.value?.id ?: "",
            isLeader = viewModel.currentUser.value?.role == Role.OWNER,
            onLeaveGroup = { viewModel.leaveGroup() },
            onPromoteMember = { userId, newRole -> viewModel.promoteMember(userId, newRole) },
            onDemoteMember = { userId -> viewModel.demoteMember(userId) }
        )

        binding.memberList.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // UI 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        Toast.makeText(context, "처리 중...", Toast.LENGTH_SHORT).show()
                    }
                    state.isSuccess -> {
                        Toast.makeText(context, "성공적으로 처리되었습니다.", Toast.LENGTH_SHORT).show()
                        if (viewModel.currentUser.value?.role == Role.OWNER) {
                            // 대표인 경우 처리
                        } else {
                            // 일반 멤버인 경우 처리
                            activity?.finish() // 그룹 나가기 성공 시 화면 종료
                        }
                    }
                    state.error != null -> {
                        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 멤버 목록 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.members.collect { members ->
                memberAdapter.updateMembers(members)
            }
        }

        // 현재 사용자 정보 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                user?.let {
                    memberAdapter = MemberAdapter(
                        currentUserId = it.id,
                        isLeader = it.role == Role.OWNER,
                        onLeaveGroup = { viewModel.leaveGroup() },
                        onPromoteMember = { userId, newRole -> viewModel.promoteMember(userId, newRole) },
                        onDemoteMember = { userId -> viewModel.demoteMember(userId) }
                    )
                    binding.memberList.adapter = memberAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentGroupName.collect { name ->
                binding.customGroupbarYellowInclude.textViewGroupName.text = name
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MembersFragment()
    }
}