package com.example.moneyhub

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // login.xml 레이아웃을 설정
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets

        }
        val button_open_create:ConstraintLayout = findViewById(R.id.button_open_create)
        button_open_create.setOnClickListener {
            val intent = Intent(this, MyPage::class.java)
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
    }
}