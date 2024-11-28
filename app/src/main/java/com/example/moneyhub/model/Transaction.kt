package com.example.moneyhub.model

// 내역 및 예산에 쓸 클래스, 구분은 verified로 할 예정
data class Transaction(
    val tid: String = "",           // 내역/예산 ID
    val gid: String = "",           // 그룹 ID
    val name: String = "",          // 내역/예산 이름
    val category: String = "",      // 카테고리
    val type: Boolean = false,      // 수입(true)/지출(false)
    val amount: Double = 0.0,       // 금액
    val content: String = "",       // 상세 내용
    val payDateEx: Long = 0L,       // 예상 결제일 (예산일 경우)
    val payDate: Long = 0L,         // 실제 결제일 (내역일 경우)
    val verified: Boolean = false,   // 내역(true)/예산(false)
    val receiptUrl: String? = null,  // 영수증 이미지 URL
    val authorId: String = "",      // 작성자 ID
    val authorName: String = "",    // 작성자 이름
    val createdAt: Long = System.currentTimeMillis()  // 생성 시간
) {

    fun getTypeText(): String = if (type) "수입" else "지출"

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tid" to tid,
            "gid" to gid,
            "name" to name,
            "category" to category,
            "type" to type,
            "amount" to amount,
            "content" to content,
            "payDateEx" to payDateEx,
            "payDate" to payDate,
            "verified" to verified,
            "receiptUrl" to receiptUrl,
            "authorId" to authorId,
            "authorName" to authorName,
            "createdAt" to createdAt
        )
    }

    companion object {

        fun fromMap(map: Map<String, Any?>): Transaction {
            return Transaction(
                tid = map["tid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                category = map["category"] as? String ?: "",
                type = map["type"] as? Boolean ?: false,
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                content = map["content"] as? String ?: "",
                payDateEx = map["payDateEx"] as? Long ?: 0L,
                payDate = map["payDate"] as? Long ?: 0L,
                verified = map["verified"] as? Boolean ?: false,
                receiptUrl = map["receiptUrl"] as? String,
                authorId = map["authorId"] as? String ?: "",
                authorName = map["authorName"] as? String ?: "",
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}