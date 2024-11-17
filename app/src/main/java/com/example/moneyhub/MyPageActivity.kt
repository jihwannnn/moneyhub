package com.example.moneyhub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.moneyhub.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding

    data class Meeting(
        val id: String,
        val name: String
    )

    // 샘플 모임 데이터
    private val meetings = listOf(
        Meeting("1", "가족 모임"),
        Meeting("2", "회사 동호회"),
        Meeting("3", "친구 여행")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupClickListeners()
        setupMeetingList()
    }

    private fun setupButtons() {
        // 정보수정 버튼 설정
        with(binding.btnEditInfo) {
            root.setBackgroundResource(R.drawable.yellow)
            btnText.apply {
                text = "정보수정"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        // 방만들기 버튼 설정
        with(binding.btnCreateRoom) {
            root.setBackgroundResource(R.drawable.emerald)
            btnText.apply {
                text = "방만들기"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    private fun setupClickListeners() {
        // 정보수정 버튼 클릭 리스너
        binding.btnEditInfo.root.setOnClickListener {
            // 정보수정 처리
        }

        // 방만들기 버튼 클릭 리스너
        binding.btnCreateRoom.root.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMeetingList() {
        // 모임 1 설정
        with(binding.meetingList.meetingItem1) {
            root.visibility = View.VISIBLE
            tvMeetingName.text = meetings.getOrNull(0)?.name ?: ""
            root.setOnClickListener {
                meetings.getOrNull(0)?.let { meeting ->
                    openMeetingDetail(meeting)
                }
            }
        }

        // 모임 2 설정
        with(binding.meetingList.meetingItem2) {
            root.visibility = View.VISIBLE
            tvMeetingName.text = meetings.getOrNull(1)?.name ?: ""
            root.setOnClickListener {
                meetings.getOrNull(1)?.let { meeting ->
                    openMeetingDetail(meeting)
                }
            }
        }

        // 모임 3 설정
        with(binding.meetingList.meetingItem3) {
            root.visibility = View.VISIBLE
            tvMeetingName.text = meetings.getOrNull(2)?.name ?: ""
            root.setOnClickListener {
                meetings.getOrNull(2)?.let { meeting ->
                    openMeetingDetail(meeting)
                }
            }
        }
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
