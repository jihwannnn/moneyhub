package com.example.moneyhub

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        setupButtons()
        setupClickListeners()
    }

    private fun setupButtons() {
        // 정보수정 버튼 설정
        findViewById<ConstraintLayout>(R.id.custom_button_include).apply {
            setBackgroundResource(R.drawable.yellow)
            findViewById<TextView>(R.id.btnText).apply {
                text = "Sign Up"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
        }
    private fun setupClickListeners() {
        // 정보수정 버튼 클릭 리스너
        findViewById<ConstraintLayout>(R.id.custom_button_include).setOnClickListener {
            // 정보수정 처리
        }

//        // 'include'된 레이아웃에서 버튼 텍스트 변경
//        val customButtonLayout = findViewById<ConstraintLayout>(R.id.custom_button_include) // include된 레이아웃
//        val buttonText = customButtonLayout.findViewById<TextView>(R.id.custom_button) // custom_button.xml 내의 TextView
//        buttonText.text = "새로운 버튼 텍스트"  // 텍스트 변경

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val custom_button_include: ConstraintLayout = findViewById(R.id.custom_button_include)
        custom_button_include.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)

        }

//        val formViewEmail = findViewById<CustomGreyFormView>(R.id.emailForm)
//        formViewEmail.setIcon(R.drawable.email)  // 아이콘 설정
//        formViewEmail.setHint("이메일")       // 힌트 설정
//
//        val formViewPassword = findViewById<CustomGreyFormView>(R.id.passwordForm)
//        formViewPassword.setIcon(R.drawable.password)  // 아이콘 설정
//        formViewPassword.setHint("비밀번호")       // 힌트 설정
    }
}
