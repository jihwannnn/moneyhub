package com.example.moneyhub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyhub.databinding.ActivityCameraBinding
import com.example.moneyhub.databinding.ActivityMyPageBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    data class Meeting(
        val id: String,
        val name: String
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



    // 모임 클릭하면 그냥 메인페이지로 이동하게 해놈
    private fun openMeetingDetail(meeting: Meeting) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("meetingId", meeting.id)
            putExtra("meetingName", meeting.name)
        }
        startActivity(intent)
    }
}
