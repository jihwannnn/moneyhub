package com.example.moneyhub.data.model

// 멤버 정보를 담는 데이터 클래스
data class Member(
    val id: String,        // 사용자 고유 ID
    val name: String,    // 멤버 이름 (예: "김회장")
    val role: String    // 멤버 상태 (예: "매니저" 또는 "멤버")
)