package com.example.moneyhub.activity.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import android.Manifest
import com.example.moneyhub.R
import com.example.moneyhub.activity.main.MainActivity
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.activity.signup.SignUpActivity
import com.example.moneyhub.databinding.ActivityLoginBinding
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    // 인텐트로부터 gid와 gname을 저장할 변수
    private val gid: String? by lazy { intent.getStringExtra("gid") }
    private val gname: String? by lazy { intent.getStringExtra("gname") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        // 인텐트에서 gid와 gname 받기
        val currentGid = gid
        val currentGname = gname

        // 모든 extras 로그 출력
        intent.extras?.keySet()?.forEach { key ->
            Log.d("LogInActivity", "Extra key: $key, value: ${intent.getStringExtra(key)}")
        }

        Log.d("LogInActivity", "onCreate - gid: $currentGid, gname: $currentGname")

         // 이미 로그인된 사용자가 있는지 확인
        if (CurrentUserSession.isLoggedIn()) {
            if (currentGid != null && currentGname != null) {
                // CurrentUserSession에 gid와 gname 설정
                val currentUser = CurrentUserSession.getCurrentUser()
                val updatedUser = currentUser.copy(
                    currentGid = currentGid,
                    currentGname = currentGname
                )
                CurrentUserSession.setCurrentUser(updatedUser)

                // MainActivity로 직접 이동
                Log.d("LogInActivity", "Navigating to MainActivity with gid: $currentGid, gname: $currentGname")
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                return
            }

            // gid와 gname이 없을 경우, 일반적인 MyPageActivity로  이동
            Log.d("LogInActivity", "Navigating to MyPageActivity without gid and gname")
            startActivity(Intent(this, MyPageActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()
        setupFormViews()
        setupButtons()
        observeViewModel()
        observeFcmState()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 인텐트에서 gid와 gname 받기
        val newGid = intent.getStringExtra("gid")
        val newGname = intent.getStringExtra("gname")

        Log.d("LogInActivity", "onNewIntent - gid: $newGid, gname: $newGname")

        if (CurrentUserSession.isLoggedIn()) {
            if (newGid != null && newGname != null) {
                // CurrentUserSession에 gid와 gname 설정
                val currentUser = CurrentUserSession.getCurrentUser()
                val updatedUser = currentUser.copy(
                    currentGid = newGid,
                    currentGname = newGname
                )
                CurrentUserSession.setCurrentUser(updatedUser)

                // MainActivity로 직접 이동
                Log.d("LogInActivity", "Navigating to MainActivity with new gid: $newGid, gname: $newGname")
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(mainIntent)
                finish()
            } else {
                // gid와 gname이 없을 경우, 일반적인 MyPageActivity로 이동
                Log.d("LogInActivity", "Navigating to MyPageActivity without new gid and gname")
                startActivity(Intent(this, MyPageActivity::class.java))
                finish()
            }
        }
    }


    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupFormViews() {
        binding.emailForm.apply {
            setIcon(R.drawable.email)
            setHint("이메일")
        }

        binding.passwordForm.apply {
            setIcon(R.drawable.password)
            setHint("비밀번호")
        }
    }

    private fun setupButtons() {

        with(binding) {
            buttonLogin.setOnClickListener {
                val email = emailForm.getText()
                val password = passwordForm.getText()
                viewModel.signIn(email, password)
            }

            buttonSignup.setOnClickListener {
                val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.initializeFcm(this)
                }

                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.POST_NOTIFICATIONS
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
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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
            initializeFcm()
        } else {
            Toast.makeText(
                this,
                "알림을 받을 수 없습니다. 설정에서 권한을 허용해주세요.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initializeFcm() {
        // 저장된 토큰 가져오기
        val sharedPrefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        val savedToken = sharedPrefs.getString("fcm_token", null)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newToken = task.result

                // 저장된 토큰과 새 토큰이 다르면 업데이트
                if (savedToken != newToken) {
                    // 토큰 저장
                    sharedPrefs.edit().putString("fcm_token", newToken).apply()

                    // 서버에 새 토큰 등록
                    Firebase.functions
                        .getHttpsCallable("updateFcmToken")
                        .call(hashMapOf("token" to newToken))
                        .addOnSuccessListener {
                            Log.d("FCM", "Token updated: $newToken")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "Failed to update token", e)
                        }
                } else {
                    Log.d("FCM", "Token unchanged")
                }
            } else {
                Log.w("FCM", "Token fetch failed", task.exception)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when {
                    state.isLoading -> {
                        Toast.makeText(this@LogInActivity, "처리 중...", Toast.LENGTH_SHORT).show()
                    }
                    state.isSuccess -> {
                        Toast.makeText(this@LogInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        val currentGid = gid
                        val currentGname = gname
                        Log.d("LogInActivity", "isSuccess - gid: $currentGid, gname: $currentGname")
                        if (currentGid != null && currentGname != null) {
                            // 로그인 성공 후, gid와 gname이 있을 경우 MainActivity로 직접 이동
                            val currentUser = CurrentUserSession.getCurrentUser()
                            val updatedUser = currentUser.copy(
                                currentGid = currentGid,
                                currentGname = currentGname
                            )
                            CurrentUserSession.setCurrentUser(updatedUser)

                            val intent = Intent(this@LogInActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            // MyPageActivity를 일반적으로 시작
                            Log.d("LogInActivity", "Navigating to MyPageActivity after login without gid and gname")
                            startActivity(Intent(this@LogInActivity, MyPageActivity::class.java))
                            finish()
                        }
                    }
                    state.error != null -> {
                        Toast.makeText(this@LogInActivity, state.error, Toast.LENGTH_SHORT).show()
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
}