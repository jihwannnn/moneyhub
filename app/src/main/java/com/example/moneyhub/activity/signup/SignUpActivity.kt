package com.example.moneyhub.activity.signup

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
import com.example.moneyhub.activity.LogInActivity
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.TestSignUpRepository
import com.example.moneyhub.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()
        setupUI()
        observeViewModel()
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.signup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI() {

        with(binding) {
            nameForm.apply {
                setIcon(R.drawable.group)
                setHint("Name")
            }

            idForm.apply {
                setIcon(R.drawable.group)
                setHint("Email")
            }

            phonenumberForm.apply {
                setIcon(R.drawable.group)
                setHint("phone number")
            }

            passwordForm.apply {
                setIcon(R.drawable.group)
                setHint("password")
            }

            passwordFormCheck.apply {
                setIcon(R.drawable.group)
                setHint("password check")
            }

            customButtonInclude.root.setOnClickListener {
                viewModel.signUp()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {

//            // 에러 메시지 관찰
//            launch {
//                viewModel.errorMessage.collect { message ->
//                    message?.let { showError(it) }
//                }
//            }
//
//            // 각 필드의 상태 관찰
//            launch {
//                viewModel.name.collect { name ->
//                    binding.nameForm.setText(name)
//                }
//            }
//
//            launch {
//                viewModel.email.collect { email ->
//                    binding.idForm.setText(email)
//                }
//            }
//
//            launch {
//                viewModel.phone.collect { phone ->
//                    binding.phonenumberForm.setText(phone)
//                }
//            }
//
//            launch {
//                viewModel.password.collect { password ->
//                    binding.passwordForm.setText(password)
//                }
//            }
//
//            launch {
//                viewModel.passwordCheck.collect { passwordCheck ->
//                    binding.passwordFormCheck.setText(passwordCheck)
//                }
//            }

            viewModel.uiState.collect { state ->
                when (state) {
                    UiState.LOADING -> return@collect //showLoading(true)
                    UiState.SUCCESS -> {
                        // showLoading(false)
                        startActivity(Intent(this@SignUpActivity, LogInActivity::class.java))
                        finish()
                    }
                    UiState.ERROR -> return@collect // showError(false)
                    UiState.INITIAL -> { }
                }
            }
        }
    }

//    private fun showLoading(show: Boolean) {
//        // binding.progressBar.isVisible = show
//    }
//
//    private fun showError(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
}
