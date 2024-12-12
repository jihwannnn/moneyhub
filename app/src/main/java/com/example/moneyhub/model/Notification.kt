package com.example.moneyhub.model

data class Notification(
    val nid: String = "",           // 알림 ID
    val gid: String = "",           // 그룹 ID
    val title: String = "",         // 알림 제목
    val content: String = "",       // 알림 내용
    val type: String = "",          // 알림 타입 (예: "TRANSACTION_ADDED", "TRANSACTION_UPDATED")
    val recipientId: String = "",   // 수신자 ID
    val data: Map<String, Any> = emptyMap(), // 추가 데이터 (예: transactionId)
    val read: Boolean = false,      // 읽음 여부
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nid" to nid,
            "gid" to gid,
            "title" to title,
            "content" to content,
            "type" to type,
            "recipientId" to recipientId,
            "data" to data,
            "read" to read,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Notification {
            @Suppress("UNCHECKED_CAST")
            return Notification(
                nid = map["nid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                title = map["title"] as? String ?: "",
                content = map["content"] as? String ?: "",
                type = map["type"] as? String ?: "",
                recipientId = map["recipientId"] as? String ?: "",
                data = map["data"] as? Map<String, Any> ?: emptyMap(),
                read = map["read"] as? Boolean ?: false,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}