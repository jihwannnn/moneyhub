package com.example.moneyhub

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneyhub.databinding.CustomMeetingItemBinding
import com.example.moneyhub.databinding.CustomMeetingListBinding

class MeetingListView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        data class Meeting(
            val id: String,
            val name: String
        )

        // MeetingListView.kt
        class MeetingListView(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
        ) : ConstraintLayout(context, attrs, defStyleAttr) {
            private val binding: CustomMeetingListBinding
            private val meetingItems = mutableListOf<CustomMeetingItemBinding>()
            private var onMeetingClickListener: ((Meeting) -> Unit)? = null

            init {
                binding = CustomMeetingListBinding.inflate(LayoutInflater.from(context), this, true)

                meetingItems.apply {
                    add(binding.meetingItem1)
                    add(binding.meetingItem2)
                    add(binding.meetingItem3)
                }
            }

            fun setMeetings(meetings: List<Meeting>) {
                meetingItems.forEach { item ->
                    item.root.visibility = View.GONE
                }

                meetings.forEachIndexed { index, meeting ->
                    if (index < meetingItems.size) {
                        val itemBinding = meetingItems[index]
                        showMeetingItem(itemBinding, meeting)
                    }
                }
            }

            private fun showMeetingItem(itemBinding: CustomMeetingItemBinding, meeting: Meeting) {
                with(itemBinding) {
                    root.visibility = View.VISIBLE
                    tvMeetingName.text = meeting.name
                    root.setOnClickListener {
                        onMeetingClickListener?.invoke(meeting)
                    }
                }
            }

            fun setOnMeetingClickListener(listener: (Meeting) -> Unit) {
                onMeetingClickListener = listener
            }
        }
    }
}