package com.example.moneyhub.model

// 현재 사용자 정보 클래스
data class CurrentUser (
    val id: String = "",        // uid
    val name: String = "",    // 이름
    val currentGid: String = "", // 현재 그룹 id
    val currentGname: String = "", // 현재 그룹 이름
    val role: Role = Role.REGULAR // 현재 역할
)