package com.example.moneyhub.data.model

import java.time.LocalDateTime

data class BoardItem(
    val id: Int,           // 게시글 ID
    val title: String,     // 제목
    val content: String,   // 내용
    val timestamp: LocalDateTime, // 게시글 작성 시간
    val commentCount: Int, // 댓글 개수
    val imageUrl: String? = null  // 이미지 URL (없으면 null)
)
