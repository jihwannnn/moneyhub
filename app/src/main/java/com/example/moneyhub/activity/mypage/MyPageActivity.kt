package com.example.moneyhub.activity.mypage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.activity.creategroup.CreateActivity
import com.example.moneyhub.activity.login.LogInActivity
import com.example.moneyhub.activity.main.MainActivity
import com.example.moneyhub.adapter.GroupAdapter
import com.example.moneyhub.databinding.ActivityMyPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()
        setupButtons()
        setupRecyclerView()
        observeViewModel()
        observeFcmState()

        // 인텐트에서 gid와 gname 받기
        val gid = intent.getStringExtra("gid")
        val gname = intent.getStringExtra("gname")
        if (gid != null && gname != null) {
            viewModel.selectGroup(gid, gname)
        }
    }

    private fun setupRecyclerView() {
        adapter = GroupAdapter(
            onGroupClick = { groupId, groupName ->
                viewModel.selectGroup(groupId, groupName)
            }
        )

        binding.groupList.apply {
            layoutManager = LinearLayoutManager(this@MyPageActivity)
            adapter = this@MyPageActivity.adapter
        }
    }

    private fun setupButtons() {
        binding.btnEditInfo.apply {
            root.setBackgroundResource(R.drawable.yellow)
            btnText.text = "정보수정"
        }

        binding.btnLogOut.apply {
            root.setBackgroundResource(R.drawable.gray)
            btnText.text = "로그아웃"
            root.setOnClickListener {
                viewModel.signOut()
            }
        }

        binding.btnCreateRoom.apply {
            root.setBackgroundResource(R.drawable.emerald)
            btnText.text = "방만들기"
            root.setOnClickListener {
                startActivity(Intent(this@MyPageActivity, CreateActivity::class.java))
            }
        }

        binding.btnSubmitLink.apply {
            root.setBackgroundResource(R.drawable.moneygrayblock)
            btnText.text = "참여"
            root.setOnClickListener {
                val groupId = binding.etMeetingLink.text.toString()
                if (groupId.isNotEmpty()) {
                    viewModel.joinGroup(groupId)
                } else {
                    Toast.makeText(this@MyPageActivity, "초대 코드를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.initializeFcm(this)
                }

                shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    AlertDialog.Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("중요한 거래내역 알림을 받기 위해서는 알림 권한이 필요합니다.")
                        .setPositiveButton("권한 요청") { _, _ ->
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton("취소") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            viewModel.initializeFcm(this)
        }
    }

    // Permission Launcher도 수정
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용되면 FCM 초기화
            viewModel.initializeFcm(this)
        } else {
            Toast.makeText(
                this,
                "알림을 받을 수 없습니다. 설정에서 권한을 허용해주세요.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                binding.tvName.text = "${user?.name ?: "Unknown"} 님"
            }
        }

        lifecycleScope.launch {
            viewModel.userGroups.collect { userGroup ->
                userGroup?.let {
                    adapter.updateData(it)
                    // 그룹 정보가 로드되었을 때 로컬 캐시에 저장
                    saveUserGroupsToLocalCache(this@MyPageActivity, it.groups)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        // Show loading if needed
                    }
                    state.successType == MyPageViewModel.SuccessType.GROUP_SELECTED -> {
                        // MainActivity로 이동
                        startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
                        finish()
                    }

                    state.successType == MyPageViewModel.SuccessType.GROUP_JOINED -> {
                        // MyPageActivity 재시작
                        Toast.makeText(this@MyPageActivity, "그룹 참여가 완료되었습니다", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }

                    state.successType == MyPageViewModel.SuccessType.SIGN_OUT -> {
                        Toast.makeText(this@MyPageActivity, "로그아웃되었습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MyPageActivity, LogInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    state.error != null -> {
                        Toast.makeText(this@MyPageActivity, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeFcmState() {
        lifecycleScope.launch {
            viewModel.fcmInitialized.collect { initialized ->
                if (initialized) {
                    Log.d("FCM", "FCM initialization successful")
                }
            }
        }
    }

    // 유저 그룹 데이터 갱신 시 호출
    fun saveUserGroupsToLocalCache(context: Context, groups: Map<String, String>) {
        val prefs = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 간단히 JSON 문자열로 저장
        val json = org.json.JSONObject().apply {
            val groupsObj = org.json.JSONObject()
            for ((gid, gname) in groups) {
                groupsObj.put(gid, gname)
            }
            put("groups", groupsObj)
        }.toString()
        editor.putString("userGroups", json)
        editor.apply()
    }
}
