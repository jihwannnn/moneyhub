package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyhub.ui.customs.CustomGreyFormView
import com.example.moneyhub.R
import com.example.moneyhub.activity.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Firebase Instance initialization
        auth = FirebaseAuth.getInstance()

        // check if it is already done login
        if (auth.currentUser != null) {
            // if it is already done login, go to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()    // set so that it does not come back when going back by finishing LogInActivity
            return
        }

        // login.xml layout 설정
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets

        }
        val button_open_create:ConstraintLayout = findViewById(R.id.button_open_create)
        button_open_create.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)

        }


        val button_open_signup:ConstraintLayout = findViewById(R.id.button_open_signup)
        button_open_signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }

        val formViewEmail = findViewById<CustomGreyFormView>(R.id.emailForm)
        formViewEmail.setIcon(R.drawable.email)  // 아이콘 설정
        formViewEmail.setHint("이메일")       // 힌트 설정

        val formViewPassword = findViewById<CustomGreyFormView>(R.id.passwordForm)
        formViewPassword.setIcon(R.drawable.password)  // 아이콘 설정
        formViewPassword.setHint("비밀번호")       // 힌트 설정

        // SignIn
        fun signIn(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // success in LogIn
                        val user = auth.currentUser
                        Toast.makeText(this, "Login Successful: ${user?.email}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()    // finishing LogInActivity
                    } else {
                        // fail in LogIn
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}