package com.example.moneyhub.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moneyhub.R
import com.example.moneyhub.databinding.ActivityRegisterDetailsBinding

class RegisterDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }


    private fun setupView() {
        // btnRegister 설정
        binding.btnRegister.apply {
            root.setBackgroundResource(R.drawable.emerald)
            linkBtnText.text = "Register"
            root.setOnClickListener {
                // 등록 처리
                finish()
            }
        }
    }

    // 안드로이드 기본 뒤로가기 버튼 처리
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}