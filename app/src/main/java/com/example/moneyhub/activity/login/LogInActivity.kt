package com.example.moneyhub.activity.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.R
import com.example.moneyhub.activity.main.MainActivity
import com.example.moneyhub.activity.mypage.MyPageActivity
import com.example.moneyhub.activity.signup.SignUpActivity
import com.example.moneyhub.databinding.ActivityLoginBinding
import com.example.moneyhub.model.sessions.CurrentUserSession
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

        // 인텐트에서 gid와 gname 받기
        val currentGid = gid
        val currentGname = gname

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
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                return
            }

            // gid와 gname이 없을 경우, 일반적인 MyPageActivity로  이동
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

        // 아이디/비밀번호 찾기
        binding.findCredentials.setOnClickListener {
            TODO()
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
                        if (currentGid != null && currentGname != null) {
                            // 로그인 성공 후, gid와 gname이 있을 경우 MainActivity로 직접 이동
                            val currentUser = CurrentUserSession.getCurrentUser()
                            val updatedUser = currentUser.copy(
                                currentGid = currentGid,
                                currentGname = currentGname
                            )
                            CurrentUserSession.setCurrentUser(updatedUser)

                            val intent = Intent(this@LogInActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // MyPageActivity를 일반적으로 시작
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
}