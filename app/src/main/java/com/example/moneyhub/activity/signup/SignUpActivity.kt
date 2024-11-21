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
import com.example.moneyhub.activity.LogInActivity
import com.example.moneyhub.common.UiState
import com.example.moneyhub.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()
        setupSignUpButton()
        observeViewModel()
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.signup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSignUpButton() {
        binding.customButtonInclude.root.setOnClickListener {
            // Get values from forms
            val name = binding.nameForm.getText()
            val email = binding.idForm.getText()
            val phone = binding.phonenumberForm.getText()
            val password = binding.passwordForm.getText()
            val passwordCheck = binding.passwordFormCheck.getText()

            // Update ViewModel
            viewModel.updateName(name)()
            viewModel.updateEmail(email)()
            viewModel.updatePhone(phone)()
            viewModel.updatePassword(password)()
            viewModel.updatePasswordCheck(passwordCheck)()

            // Trigger sign up
            viewModel.signUp()
        }
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    UiState.LOADING -> {
                        // Show loading indicator if needed
                    }
                    UiState.SUCCESS -> {
                        Toast.makeText(this@SignUpActivity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LogInActivity::class.java))
                        finish()
                    }
                    UiState.ERROR -> {
                        // Error handling will be done through errorMessage flow
                    }
                    else -> {
                        // Handle other states if needed
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                message?.let {
                    Toast.makeText(this@SignUpActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
