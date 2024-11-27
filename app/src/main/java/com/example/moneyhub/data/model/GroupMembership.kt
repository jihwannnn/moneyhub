package com.example.moneyhub.data.model

data class GroupMembership(
    val uid: String = "",        // 사용자 ID
    val gid: String = "",       // 그룹 ID
    val userName: String = "",      // 사용자 이름
    val role: Role = Role.REGULAR,  // 역할
    val joinedAt: Long = System.currentTimeMillis()  // 가입 시간
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "gid" to gid,
            "userName" to userName,
            "role" to role.name,
            "joinedAt" to joinedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): GroupMembership {
            return GroupMembership(
                uid = map["uid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                role = Role.fromName(map["role"] as? String ?: "REGULAR"),
                joinedAt = map["joinedAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}