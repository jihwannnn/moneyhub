package com.example.moneyhub.data.model

data class GroupMembership(
    val userId: String = "",        // 사용자 ID
    val groupId: String = "",       // 그룹 ID
    val userName: String = "",      // 사용자 이름
    val role: Role = Role.REGULAR,  // 역할
    val joinedAt: Long = System.currentTimeMillis()  // 가입 시간
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "groupId" to groupId,
            "userName" to userName,
            "role" to role.name,
            "joinedAt" to joinedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): GroupMembership {
            return GroupMembership(
                userId = map["userId"] as? String ?: "",
                groupId = map["groupId"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                role = Role.fromName(map["role"] as? String ?: "REGULAR"),
                joinedAt = map["joinedAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}