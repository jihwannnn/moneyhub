package com.example.moneyhub.model

class User(
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
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                uid = map["uid"] as? String ?: "",
                groups = map["groups"] as? Map<String, String> ?: emptyMap()
            )
        }
    }
}