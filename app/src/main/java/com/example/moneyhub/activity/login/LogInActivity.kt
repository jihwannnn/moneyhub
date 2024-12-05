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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

         // 이미 로그인된 사용자가 있는지 확인
        if (CurrentUserSession.isLoggedIn()) {
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
                        // Show loading indicator
                    }
                    state.isSuccess -> {
                        Toast.makeText(this@LogInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LogInActivity, MyPageActivity::class.java))
                        finish()
                    }
                    state.error != null -> {
                        Toast.makeText(this@LogInActivity, state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}