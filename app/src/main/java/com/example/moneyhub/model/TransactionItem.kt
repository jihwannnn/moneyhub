package com.example.moneyhub.model

data class TransactionItem(

    val tid:Long,          // 거래별 고유 id

    val date: String,        // 날짜 (예: "2024-11-19")

    val icon: Int,           // 아이콘 이미지 리소스 ID (예: R.drawable.icon_image
    val title: String,       // 제목
    val category: String,    // 카테고리 이름
    val amount: Double,        // 입출금내역
    val type: Boolean = false,      // 수입(true)/지출(false)
)


