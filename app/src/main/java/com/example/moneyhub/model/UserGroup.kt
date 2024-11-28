package com.example.moneyhub.model

// 유저가 속해있는 그룹에 대한 정보
class UserGroup(
    val uid: String = "",  // uid
    val groups: Map<String, String> = emptyMap()  // 유저가 가입한 그룹 map<id, name>
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "groups" to groups
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any?>): UserGroup {
            return UserGroup(
                uid = map["uid"] as? String ?: "",
                groups = map["groups"] as? Map<String, String> ?: emptyMap()
            )
        }
    }
}