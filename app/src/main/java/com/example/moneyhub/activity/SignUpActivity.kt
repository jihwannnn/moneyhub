package com.example.moneyhub.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moneyhub.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)


        // 'include'된 레이아웃의 버튼 텍스트 변경
//        val buttonText = findViewById<TextView>(R.id.custom_button_include)
//            .findViewById<TextView>(R.id.custom_button)
//        buttonText.text = "새로운 버튼 텍스트"

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
