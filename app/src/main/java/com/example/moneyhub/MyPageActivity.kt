package com.example.moneyhub

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        setupButtons()
        setupClickListeners()
    }

    private fun setupButtons() {
        // 정보수정 버튼 설정
        findViewById<ConstraintLayout>(R.id.btnEditInfo).apply {
            setBackgroundResource(R.drawable.yellow)
            findViewById<TextView>(R.id.btnText).apply {
                text = "정보수정"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        // 방만들기 버튼 설정
        findViewById<ConstraintLayout>(R.id.btnCreateRoom).apply {
            setBackgroundResource(R.drawable.emerald)
            findViewById<TextView>(R.id.btnText).apply {
                text = "방만들기"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    private fun setupClickListeners() {
        // 정보수정 버튼 클릭 리스너
        findViewById<ConstraintLayout>(R.id.btnEditInfo).setOnClickListener {
            // 정보수정 처리
        }

        // 방만들기 버튼 클릭 리스너
        val btnCreateRoom: ConstraintLayout = findViewById(R.id.btnCreateRoom)
        btnCreateRoom.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }
}