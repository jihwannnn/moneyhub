package com.example.moneyhub.model

// jh 그룹 기본 정보
data class Group(
    val gid: String = "",           // 그룹 ID
    val name: String = "",          // 그룹 이름
    val inviteCode: String = "",    // 초대 코드
    val ownerId: String = "",       // 모임장 ID
    val ownerName: String = "",     // 모임장 이름
    val memberCount: Int = 1,       // 멤버 수
    val createdAt: Long = System.currentTimeMillis()  // 생성 시간
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gid" to gid,
            "name" to name,
            "inviteCode" to inviteCode,
            "ownerId" to ownerId,
            "ownerName" to ownerName,
            "memberCount" to memberCount,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Group {
            return Group(
                gid = map["gid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                inviteCode = map["inviteCode"] as? String ?: "",
                ownerId = map["ownerId"] as? String ?: "",
                ownerName = map["ownerName"] as? String ?: "",
                memberCount = (map["memberCount"] as? Long)?.toInt() ?: 1,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}