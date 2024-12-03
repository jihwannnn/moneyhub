package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyhub.R
import com.example.moneyhub.activity.signup.SignUpActivity
import com.example.moneyhub.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Instance initialization
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        setupWindowInsets()
        setupClickListeners()
        setupFormViews()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            buttonLogin.setOnClickListener {
                val intent = Intent(this@LogInActivity, MyPageActivity::class.java)
                startActivity(intent)
            }

            buttonSignup.setOnClickListener {
                val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupFormViews() {
        with(binding) {
            // Setup email form
            emailForm.apply {
                setIcon(R.drawable.email)
                setHint("이메일")
            }

            // Setup password form
            passwordForm.apply {
                setIcon(R.drawable.password)
                setHint("비밀번호")
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        Toast.makeText(this, "Login Successful: ${it.email}", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}