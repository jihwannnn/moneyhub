package com.example.moneyhub.model

// jh 그룹의 멤버들 정보 (role 포함)
data class Membership(
    val uid: String = "",        // 사용자 ID
    val gid: String = "",       // 그룹 ID
    val userName: String = "",      // 사용자 이름
    val role: Role = Role.REGULAR,  // 역할
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "gid" to gid,
            "userName" to userName,
            "role" to role.name
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Membership {
            return Membership(
                uid = map["uid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                role = Role.fromName(map["role"] as? String ?: "REGULAR")
            )
        }
    }
}