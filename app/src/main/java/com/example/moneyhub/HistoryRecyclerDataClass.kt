package com.example.moneyhub

data class HistoryRecyclerDataClass(
    val icon: Int,           // 아이콘 이미지 리소스 ID (예: R.drawable.icon_image
    val title: String,       // 제목
    val category: String,    // 카테고리 이름
    val transaction: Double        // 입출금내역
)
